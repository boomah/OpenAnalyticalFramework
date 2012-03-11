package com.openaf.properties

import api.PropertiesService
import org.osgi.framework.{BundleContext, BundleActivator}
import java.io.{File, FileReader}
import java.util.Properties

class PropertiesBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    context.registerService(classOf[PropertiesService], new PropertiesServiceImpl, null)
  }

  def stop(context:BundleContext) {}
}

class PropertiesServiceImpl extends PropertiesService {
  private val userDefinedProperties = {
    val propertiesFile = new File("openaf.properties")
    val properties = new Properties
    if (propertiesFile.exists) {
      properties.load(new FileReader(propertiesFile))
    }
    properties
  }

  def name = "Test"
  def externalURL = ""
}
