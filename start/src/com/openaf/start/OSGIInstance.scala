package com.openaf.start

import org.osgi.framework.launch.FrameworkFactory
import java.util.{ServiceLoader, HashMap}
import org.osgi.framework.{FrameworkEvent, FrameworkListener}
import java.net.{ServerSocket, ConnectException, Socket}
import java.io.File
import org.osgi.framework.wiring.FrameworkWiring

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

  private def excludedPackages(jarFile:File):List[String] = {
    val name = jarFile.getName.toLowerCase
    if (name.startsWith("log4j")) {
      List("com.ibm.uvm.tools", "com.sun.jdmk.comm", "javax.jmdns", "javax.jms", "javax.mail", "javax.mail.internet")
    } else {
      Nil
    }
  }

  def main(args: Array[String]) {
    val s = File.separator
    val formattedArgs = args.map(_.trim().toLowerCase).toList.sorted
    val serverArg = "server"
    val guiArg = "gui"
    if (formattedArgs.size == 0 || formattedArgs.size > 2 || formattedArgs.exists(arg => {arg != serverArg && arg != guiArg})) {
      System.err.println("Args can be either \"gui\", \"server\" or both")
      System.exit(-1)
    }

    // TODO - moduleMap should be specified by the build system or project file.
    val moduleMap = Map(
      serverArg -> List("test", "testConsumer", "utils"),
      guiArg -> Nil
    )

    val argsString = "osgi" + s + formattedArgs.mkString

    def bundles(modules:List[String]) = {
      val moduleBundleDefinitions = modules.flatMap(topLevelModuleName => {
        val subModules = FileUtils.subModules(topLevelModuleName)
        subModules.map(subModule => ModuleBundleDefinition(topLevelModuleName, subModule))
      })

      val libraryBundleDefinitions = List(
        SimpleLibraryBundleDefinition("Scala", new File("lib" + s + "scala-library.jar"))
      ) ::: modules.flatMap(module => FileUtils.exportedLibraries(module)).map(jarFile => LibraryBundleDefinition(jarFile, excludedPackages(jarFile)))

      SimpleBundleDefinitions(List("sun.misc"), libraryBundleDefinitions ::: moduleBundleDefinitions)
    }

    def configs = moduleMap.filter{case (typeName, _) => formattedArgs.contains(typeName)}
            .map{case (typeName, modules) => (typeName -> bundles(modules))}
            .map{case (typeName, bundleDefinitions) => OSGIInstanceConfig(argsString + "-" + typeName, () => Map(), bundleDefinitions)}.toList

    startOrTrigger(argsString, configs _)
  }
}

