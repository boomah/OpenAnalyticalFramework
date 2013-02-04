package com.openaf.travel

import api.TravelPageDataFacility
import com.openaf.osgi.OSGIUtils
import org.osgi.framework.{BundleActivator, BundleContext}

class TravelBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("TravelBundleActivator started")
    val dictionary = OSGIUtils.mapToDictionary(Map(OSGIUtils.ExportService -> true))
    context.registerService(classOf[TravelPageDataFacility], new TravelPageDataFacilityImpl, dictionary)
  }
  def stop(context:BundleContext) {
    println("TravelBundleActivator stopped")
  }
}
