package com.openaf.start

import org.osgi.framework.launch.FrameworkFactory
import java.util.{ServiceLoader, HashMap}
import org.osgi.framework.{FrameworkEvent, FrameworkListener}
import java.net.{ServerSocket, ConnectException, Socket, URL, SocketException}
import org.osgi.framework.wiring.FrameworkWiring
import collection.mutable.ListBuffer
import java.io._
import java.util.concurrent.CopyOnWriteArraySet
import java.util.jar.JarFile

class OSGIInstance(name:String, bundles:BundleDefinitions) {
  private val framework = {
    val frameworkProps = {
      val hm = new HashMap[String, String]
      hm.put("org.osgi.framework.storage", name)
      hm.put("org.osgi.framework.bootdelegation", "sun.*,com.sun.*")
      hm.put("org.osgi.framework.system.packages.extra", bundles.systemPackages.mkString(","))
      hm
    }
    val framework = ServiceLoader.load(classOf[FrameworkFactory], getClass.getClassLoader).iterator.next.newFramework(frameworkProps)
    framework.init()
    framework.getBundleContext.addFrameworkListener(new FrameworkListener() {
      def frameworkEvent(event:FrameworkEvent) {
        if (event.getThrowable != null) {
          event.getThrowable.printStackTrace()
        }
      }
    })
    framework
  }

  def update() {
    val context = framework.getBundleContext
    val currentBundles = context.getBundles.map(bundle => BundleName(bundle.getSymbolicName, bundle.getVersion) -> bundle)
            .filter{case (_, bundle) => (bundle.getBundleId != 0)}.toMap
    val newBundles = bundles.bundles.map(definition => definition.name -> definition).toMap

    val ignoredBundles:Map[String, List[BundleDefinition]] = bundles.bundles.groupBy(_.name.name).filter(_._2.size > 1).mapValues(_.init)

    println("Ignored bundles: " + ignoredBundles.flatMap(_._2.map(_.name)).mkString(", "))

    val unInstalled = (currentBundles.keySet -- newBundles.keySet).toList.map(bundleToRemove => {
      currentBundles(bundleToRemove).uninstall()
      currentBundles(bundleToRemove)
    })

    println("Uninstalled bundles: " + unInstalled.map(r => (r.getSymbolicName + " : " + r.getVersion)).mkString(", "))

    val updated = (newBundles.keySet & currentBundles.keySet).toList.flatMap(commonBundle => {
      val newBundleDef = newBundles(commonBundle)
      val currentBundle = currentBundles(commonBundle)
      if (newBundleDef.lastModified > currentBundle.getLastModified) {
        println("Updating: " + currentBundle.getSymbolicName + "...")
        currentBundle.update(newBundleDef.inputStream)
        println("Updated: %s (state: %s)".format(currentBundle.getSymbolicName, currentBundle.getState))
        Some(currentBundle)
      } else {
        None
      }
    })

    println("Updated bundles: " + updated.map(_.getSymbolicName).mkString(", "))

    val installed = (newBundles.keySet -- currentBundles.keySet).toList.map(newBundleName => {
      println("Installing: " + newBundleName + " : " + newBundleName.version + "...")
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

object OSGIInstance {
  def commonSystemPackages = List("sun.misc")
}

case class OSGIInstanceConfig(name:String, properties:()=>Map[String,String], bundles:BundleDefinitions)

object ServerOSGIInstanceStarter {
  private val GUIBundlesDir = new File("gui-bundle-cache")
  if (!GUIBundlesDir.exists) GUIBundlesDir.mkdir
  val TopLevel = "osgi" + File.separator

  private def fileNamesToMap(fileNames:List[String]) = {
    fileNames.map(fileName => {
      val fileNameNoExtension = fileName.stripSuffix(".jar")
      val (start, end) = fileNameNoExtension.splitAt(fileNameNoExtension.lastIndexOf("-"))
      (start -> end.tail)
    }).toMap
  }

  private def updateGUIConfigOnDisk(config:OSGIInstanceConfig) = {
    val currentFiles = GUIBundlesDir.listFiles().filter(file => file.getName.toLowerCase.endsWith(".jar")).toList
    val currentFileNames = currentFiles.map(_.getName)
    val currentVersions = fileNamesToMap(currentFileNames)

    val configNameToDef = config.bundles.bundles.map(bundleDef => (bundleDef.name.name.replaceAll("\\.", "-") -> bundleDef)).toMap
    val configFileNames = configNameToDef.map{case (name, bundleDef) => name + "-" + bundleDef.lastModified}.toList
    val configVersions = fileNamesToMap(configFileNames)

    val unnecessaryNames = (currentVersions.keySet -- configVersions.keySet)
    unnecessaryNames.foreach(name => {
      val file = new File(GUIBundlesDir, name + "-" + currentVersions(name) + ".jar")
      file.delete
    })

    val missingOrOutOfDateMap = configVersions.flatMap{case (configName, configVersion) => {
      if (!currentVersions.contains(configName) || (configVersion != currentVersions(configName))) {
        Some((configName -> configVersion))
      } else {
        None
      }
    }}

    missingOrOutOfDateMap.foreach{case (name,version) => {
      if (currentVersions.contains(name)) {
        val file = new File(GUIBundlesDir, name + "-" + currentVersions(name) + ".jar")
        file.delete
      }
      val file = new File(GUIBundlesDir, name + "-" + version + ".jar")
      val outputStream = new BufferedOutputStream(new FileOutputStream(file))
      val bundleInputStream = configNameToDef(name).inputStream
      FileUtils.copyStreams(bundleInputStream, outputStream)
    }}

    unnecessaryNames.nonEmpty || missingOrOutOfDateMap.nonEmpty
  }

  def startOrTrigger(configName:String, guiConfigFunction:()=>OSGIInstanceConfig, serverConfigFunction:()=>OSGIInstanceConfig) {
    val port = 1024 + ((configName.hashCode.abs % 6400) * 10) + 9
    try {
      val socket = new Socket("localhost", port)
      socket.close()
      println("Triggered reload")
    } catch {
      case e:ConnectException => {
        val guiConfig = guiConfigFunction()
        updateGUIConfigOnDisk(guiConfig)
        val serverConfig = serverConfigFunction()
        val serverInstance = new OSGIInstance(serverConfig.name, serverConfig.bundles)
        serverInstance.start()

        val clientsToUpdate = new CopyOnWriteArraySet[Socket]

        new Thread(new Runnable {
          def run() {
            val updateSocket = new ServerSocket(7778)
            while (true) {
              val client = updateSocket.accept
              clientsToUpdate.add(client)
            }
          }
        }, "osgi-gui-update-listener").start()

        new Thread(new Runnable {
          def run() {
            val server = new ServerSocket(port)
            while (true) {
              val client = server.accept
              client.close()
              val newGuiConfig = guiConfigFunction()
              val guiModulesUpdated = updateGUIConfigOnDisk(newGuiConfig)
              serverInstance.update()
              if (guiModulesUpdated) {
                val clientsToUpdateIterator = clientsToUpdate.iterator
                while (clientsToUpdateIterator.hasNext) {
                  val clientToUpdate = clientsToUpdateIterator.next
                  try {
                    val outputStream = clientToUpdate.getOutputStream
                    outputStream.write(1)
                  } catch {
                    case e:SocketException => clientsToUpdate.remove(clientToUpdate)
                  }
                }
              }
            }
          }
        }, "osgi-reload-listener").start()
      }
    }
  }

  def excludedPackages(jarFile:File) = {
    val name = jarFile.getName.toLowerCase
    if (name.startsWith("log4j")) {
      List("com.ibm.uvm.tools", "com.sun.jdmk.comm", "javax.jmdns", "javax.jms", "javax.mail", "javax.mail.internet")
    } else {
      Nil
    }
  }

  def formattedSubNames(file:File) = file.listFiles().toList.map(_.getName.trim()).filterNot(_.toLowerCase == ".ds_store")
  def componentsModulesDir = new File("modules-components")
  def moduleDir(module:String) = new File(componentsModulesDir, module)
  def modules = formattedSubNames(componentsModulesDir)
  def systemPackages = OSGIInstance.commonSystemPackages
  def commonOSGIJARBundleDefinitions = {
    new File("common-bundles").listFiles.filter(_.getName.trim.toLowerCase.endsWith(".jar"))
      .map(OSGIJARBundleDefinition).toList
  }
  def globalLibraryBundleDefinitions = List(
    SimpleLibraryBundleDefinition("Scala", new File("lib" + File.separator + "scala-library.jar"))
  )
  def serverOSGIJARBundleDefinitions = {
    new File("server-bundles").listFiles.filter(_.getName.trim.toLowerCase.endsWith(".jar"))
      .map(OSGIJARBundleDefinition).toList
  }
}

class GUIUpdater(baseURL:URL, instanceName:String) {
  private val proxy = java.net.Proxy.NO_PROXY
  private val configURL = new URL(baseURL + "/osgigui/")
  private val tmpDir = new File(System.getProperty("java.io.tmpdir"))
  private val rootCacheDir = new File(tmpDir, "openaf")
  private val cacheDirName = {
    instanceName + "-" + configURL.getHost + (if (configURL.getPort == 80) "" else ("-" + configURL.getPort))
  }
  private val cacheDir = new File(rootCacheDir, cacheDirName)

  private def readLines(in:InputStream) = {
    val bufferedReader = new BufferedReader(new InputStreamReader(in))
    val lines = (Iterator continually (bufferedReader.readLine()) takeWhile (_ != null)).toList
    in.close()
    lines
  }

  private def openConnection(url:URL) = url.openConnection(proxy).getInputStream

  private def readLatestFromServer = {
    val latestInputStream = openConnection(configURL)
    val latestLines = readLines(latestInputStream)
    latestLines.map(line => {
      val components = line.split(" ")
      OSGIJARConfig(components(0), components(1), components(2).toLong)
    })
  }

  private def generateInputStream(osgiJARConfig:OSGIJARConfig) = {
    val name = osgiJARConfig.symbolicName + "-" + osgiJARConfig.version + "-" + osgiJARConfig.timestamp.toString
    val jarURL = new URL(baseURL + "/osgigui/" + name)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    FileUtils.copyStreams(openConnection(jarURL), byteArrayOutputStream)
    new ByteArrayInputStream(byteArrayOutputStream.toByteArray)
  }

  private def guiBundleDefinitions = {
    val osgiJARConfigs = readLatestFromServer
    osgiJARConfigs.map(osgiJARConfig => {
      new RemoteOSGIJARBundleDefinition(osgiJARConfig, (config) => generateInputStream(config))
    })
  }
  private val javaFXPackages = {
    val javaFXJarFile = new JarFile(System.getProperty("java.home") + "/lib/jfxrt.jar")
    val entries = javaFXJarFile.entries
    val packageBuffer = new ListBuffer[String]
    while (entries.hasMoreElements) {
      val entry = entries.nextElement
      if (entry.isDirectory) {
        packageBuffer += entry.getName
      }
    }
    packageBuffer.toList.map(_.replaceAll("/", ".").dropRight(1))
  }
  private def systemPackages = OSGIInstance.commonSystemPackages ::: javaFXPackages

  def guiConfig:OSGIInstanceConfig = {
    val simpleBundleDefinitions = new SimpleBundleDefinitions(systemPackages _, guiBundleDefinitions _)
    val configName = cacheDir.getPath + File.separator + "osgiData"
    OSGIInstanceConfig(configName, () => Map(), simpleBundleDefinitions)
  }
}

case class OSGIJARConfig(symbolicName:String, version:String, timestamp:Long)
