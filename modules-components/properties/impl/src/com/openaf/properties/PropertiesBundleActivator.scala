package com.openaf.properties

import api.PropertiesService
import org.osgi.framework.{BundleContext, BundleActivator}
import java.io.File
import io.Source

class PropertiesBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    context.registerService(classOf[PropertiesService], new PropertiesServiceImpl, null)
  }
  def stop(context:BundleContext) {}
}

class PropertiesServiceImpl extends PropertiesService {
  private val userDefinedProperties = {
    val propertiesFile = new File("openaf.properties")
    if (propertiesFile.exists) {
      val source = Source.fromFile(propertiesFile)
      val lines = source.getLines.toList
      source.close()
      lines.map(line => {
        val (start, end) = line.splitAt(line.indexOf("="))
        (start.trim.toLowerCase -> end.trim.tail)
      }).toMap
    } else {
      Map[String,String]()
    }
  }

  def name = userDefinedProperties.getOrElse("name", "Test")
  def externalURL = userDefinedProperties.getOrElse("externalurl", "http://localhost:7777").toLowerCase
}
