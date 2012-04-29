package com.openaf.utils

object Utils {
  val UtilsString = "-- Utils are GREAT! --"
  val ActualJavaVersion = {
    val javaVersion = System.getProperty("java.version")
    val (_ :: major :: update :: Nil) = javaVersion.split("\\.").toList
    JavaVersion(major.toInt, update.takeRight(2).toInt)
  }
  def javaVersionValid(minimumJavaVersion:JavaVersion) = {
    ((ActualJavaVersion.major >= minimumJavaVersion.major) && (ActualJavaVersion.update >= minimumJavaVersion.update))
  }
}