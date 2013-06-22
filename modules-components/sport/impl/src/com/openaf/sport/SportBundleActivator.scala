package com.openaf.sport

import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.osgi.OSGIUtils
import com.openaf.sport.api.SportPageDataFacility

class SportBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("SportBundleActivator started")
    val dictionary = OSGIUtils.mapToDictionary(Map(OSGIUtils.ExportService -> true))
    context.registerService(classOf[SportPageDataFacility], new SportPageDataFacilityImpl, dictionary)
  }
  def stop(context:BundleContext) {
    println("SportBundleActivator stopped")
  }
}
