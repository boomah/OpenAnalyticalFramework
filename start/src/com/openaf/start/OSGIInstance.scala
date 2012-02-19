package com.openaf.start

import org.osgi.framework.launch.FrameworkFactory
import java.util.{ServiceLoader, HashMap}
import org.osgi.framework.{FrameworkEvent, FrameworkListener}
import java.net.{ServerSocket, ConnectException, Socket}
import java.io.File
import org.osgi.framework.wiring.FrameworkWiring
import collection.mutable.ListBuffer

class OSGIInstance(name:String, bundles:BundleDefinitions) {
  private val framework = {
    val frameworkProps = {
      val hm = new HashMap[String, String]
      hm.put("org.osgi.framework.storage", name)
      hm.put("org.osgi.framework.bootdelegation", "sun.*,com.sun.*")
      hm.put("org.osgi.framework.system.packages.extra", bundles.systemPackages.mkString(","))
      hm
    }
    val fw = ServiceLoader.load(classOf[FrameworkFactory], getClass.getClassLoader).iterator.next.newFramework(frameworkProps)
    fw.init()
    fw.getBundleContext.addFrameworkListener(new FrameworkListener() {
      def frameworkEvent(event:FrameworkEvent) {
        if (event.getThrowable != null) {
          event.getThrowable.printStackTrace()
        }
      }
    })
    fw
  }

  def update() {
    val context = framework.getBundleContext
    val currentBundles = context.getBundles.map(bundle => BundleName(bundle.getSymbolicName, bundle.getVersion) -> bundle).toMap.filter(_._2.getBundleId != 0)
    val newBundles = bundles.bundles.map(definition => definition.name -> definition).toMap

    val ignoredBundles:Map[String, List[BundleDefinition]] = bundles.bundles.groupBy(_.name.name).filter(_._2.size > 1).mapValues(_.init)

    println("Ignored bundles: " + ignoredBundles.flatMap(_._2.map(_.name)).mkString(", "))

    // Uninstall, update, install, refresh & start.
    val unInstalled = (currentBundles.keySet -- newBundles.keySet).toList.map{bundleToRemove => currentBundles(bundleToRemove).uninstall(); currentBundles(bundleToRemove)}

    println("Uninstalled bundles: " + unInstalled.map(_.getSymbolicName).mkString(", "))

    val updated = (newBundles.keySet & currentBundles.keySet).toList.flatMap(commonBundle => {
      val newBundleDef = newBundles(commonBundle)
      val currentBundle = currentBundles(commonBundle)
      if (newBundleDef.lastModified > currentBundle.getLastModified|| newBundleDef.name.name == "auth") {
        println("Updating: " + currentBundle.getSymbolicName + "...")
        currentBundle.update(newBundleDef.inputStream)
        println("Updated: %s (state: %s)".format(currentBundle.getSymbolicName, currentBundle.getState))
        Some( currentBundle )
      } else {
        None
      }
    })

    println("Updated bundles: " + updated.map(_.getSymbolicName).mkString(", "))

    val installed = (newBundles.keySet -- currentBundles.keySet).toList.map(newBundleName => {
      println("Installing: " + newBundleName + "...")
      val newBundleDef = newBundles(newBundleName)
      val res = context.installBundle("from-bnd:" + newBundleDef.name, newBundleDef.inputStream)
      println("Installed: %s (state: %s)".format(newBundleName, res.getState))
      res
    })

    println("Installed bundles: " + installed.map(_.getSymbolicName).mkString(", "))

    if (unInstalled.nonEmpty || updated.nonEmpty) {
      val packageAdmin = context.getBundle.adapt(classOf[FrameworkWiring])
      packageAdmin.refreshBundles(null, Array())
    }
    installed.foreach(_.start())
  }

  def start() {
    update()
    framework.start()
  }

  def stop() {
    framework.stop()
  }
}

case class OSGIInstanceConfig(name:String, properties:()=>Map[String,String], bundles:BundleDefinitions)

object OSGIInstanceStarter {
  private def startOrTrigger(configName:String, configsFunction: () => List[OSGIInstanceConfig]) {
    val port = 1024 + ((configName.hashCode.abs % 6400) * 10) + 9
    try {
      val socket = new Socket("localhost", port)
      socket.close()
      println("Triggered reload")
    } catch {
      case e:ConnectException => {
        val configs = configsFunction()
        val instances = configs.map(config => {
          val instance = new OSGIInstance(config.name, config.bundles)
          instance.start()
          instance
        })
        new Thread(new Runnable() { def run() {
          val server = new ServerSocket(port)
          println("Listening on port " + port)
          while (true) {
            val client = server.accept()
            client.close()
            instances.foreach(_.update())
          }
        } }, "osgi-reload-listener").start()
      }
    }
  }

  private def excludedPackages(jarFile:File) = {
    val name = jarFile.getName.toLowerCase
    if (name.startsWith("log4j")) {
      List("com.ibm.uvm.tools", "com.sun.jdmk.comm", "javax.jmdns", "javax.jms", "javax.mail", "javax.mail.internet")
    } else {
      Nil
    }
  }

  private def formattedSubNames(file:File) = file.listFiles().toList.map(_.getName.trim())
  private def componentsModulesDir = new File("modules-components")
  private def moduleDir(module:String) = new File(componentsModulesDir, module)
  private def modules = formattedSubNames(componentsModulesDir)
  private def serverModules = modules.filter(module => {
    val moduleDirs = formattedSubNames(moduleDir(module))
    moduleDirs.contains("api") || moduleDirs.contains("impl")
  })
  private def guiModules = modules.filter(module => {
    moduleDir(module).listFiles().map(_.getName.trim().toLowerCase).contains("gui")
  })
  private def serverModulesBundleDefinitions = serverModules.flatMap(module => {
    val moduleDirs = formattedSubNames(moduleDir(module))
    val lb = new ListBuffer[ModuleBundleDefinition]()
    if (moduleDirs.contains("api")) {
      lb += ModuleBundleDefinition(module, ModuleType.API)
    }
    if (moduleDirs.contains("impl")) {
      lb += ModuleBundleDefinition(module, ModuleType.IMPL)
    }
    lb.toList
  })
  private def guiModulesBundleDefinitions = guiModules.map(module => ModuleBundleDefinition(module, ModuleType.GUI))

  private def systemPackages = List("sun.misc")
  private def globalLibraryBundleDefinitions = List(
    SimpleLibraryBundleDefinition("Scala", new File("lib" + File.separator + "scala-library.jar"))
  )

  def main(args: Array[String]) {
    val formattedArgs = args.map(_.trim().toLowerCase).toList.sorted
    val serverArg = "server"
    val guiArg = "gui"
    if (formattedArgs.size == 0 || formattedArgs.size > 2 || formattedArgs.exists(arg => {arg != serverArg && arg != guiArg})) {
      System.err.println("Args can be either \"gui\", \"server\" or both")
      System.exit(-1)
    }
    val startGUI = formattedArgs.contains(guiArg)
    val startServer = formattedArgs.contains(serverArg)

    // TODO - moduleTypeLibraryMap should be specified by the build system or project file.
    val moduleTypeLibraryMap = Map(
      guiArg -> Nil,
      serverArg -> List("utils")
    )

    val argsString = "osgi" + File.separator + formattedArgs.mkString

    def configs = {
      val serverConfig = if (startServer) {
        val serverLibraryBundleDefinitions = moduleTypeLibraryMap(serverArg).map(library => ModuleBundleDefinition(library, ModuleType.Library))
        val serverBundleDefinitions = SimpleBundleDefinitions(systemPackages, globalLibraryBundleDefinitions ::: serverModulesBundleDefinitions ::: serverLibraryBundleDefinitions)
        List(OSGIInstanceConfig(argsString + "-" + serverArg, () => Map(), serverBundleDefinitions))
      } else {
        Nil
      }
      val guiConfig = if (startGUI) {
        val guiLibraryBundleDefinitions = moduleTypeLibraryMap(guiArg).map(library => ModuleBundleDefinition(library, ModuleType.Library))
        val guiBundleDefinitions = SimpleBundleDefinitions(systemPackages, globalLibraryBundleDefinitions ::: guiModulesBundleDefinitions ::: guiLibraryBundleDefinitions)
        List(OSGIInstanceConfig(argsString + "-" + serverArg, () => Map(), guiBundleDefinitions))
      } else {
        Nil
      }
      serverConfig ::: guiConfig
    }

    startOrTrigger(argsString, configs _)
  }
}

