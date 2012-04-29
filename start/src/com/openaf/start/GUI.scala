package com.openaf.start

import java.net.{URL, Socket}

object GUI {
  def main(args:Array[String]) {
    import StartUtils._
    if (!javaVersionValid(MinimumJavaVersion)) {
      throw new IllegalStateException(
        "You need Java %s update %s or above to run OpenAF. ".format(MinimumJavaVersion.major, MinimumJavaVersion.update) +
        "You currently have Java %s update %s.".format(ActualJavaVersion.major, ActualJavaVersion.update))
    }
    val baseURL = new URL(args(0))
    val guiUpdater = new GUIUpdater(baseURL, args(1))
    val guiConfig = guiUpdater.guiConfig
    val guiInstance = new OSGIInstance(guiConfig.name, guiConfig.bundles)
    guiInstance.start()

    val hostForUpdate = baseURL.getHost
    val portForUpdates = args(2).toInt
    val socketForUpdate = new Socket(hostForUpdate, portForUpdates)
    val inputStream = socketForUpdate.getInputStream
    while (true) {
      inputStream.read
      println("^^^ Update GUI")
      guiInstance.update()
    }
  }
}

object StartUtils {
  val MinimumJavaVersion = JavaVersion(7, 4)
  val ActualJavaVersion = {
    val javaVersion = System.getProperty("java.version")
    val (_ :: major :: update :: Nil) = javaVersion.split("\\.").toList
    JavaVersion(major.toInt, update.takeRight(2).toInt)
  }
  def javaVersionValid(minimumJavaVersion:JavaVersion) = {
    ((ActualJavaVersion.major >= minimumJavaVersion.major) && (ActualJavaVersion.update >= minimumJavaVersion.update))
  }
}
case class JavaVersion(major:Int, update:Int)