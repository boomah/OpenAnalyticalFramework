package com.openaf.osgi

import collection.mutable
import scala.collection.JavaConverters._

object OSGIUtils {
  val ExportService = "com.openaf.exportService"

  def mapToDictionary(map:Map[String,Any]) = {
    val props = new mutable.HashMap[String,Any]()
    map.foreach {
      case (key, value) => props.put(key, value)
    }
    props.asJavaDictionary
  }
}
