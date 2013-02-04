package com.openaf.viewer

import api.ViewerPageDataFacility
import com.openaf.osgi.OSGIUtils
import org.osgi.framework.{BundleActivator, BundleContext}

class ViewerBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("------------------------------------")
    println("Viewer Bundle Activator impl started")
    println("------------------------------------")
    val dictionary = OSGIUtils.mapToDictionary(Map(OSGIUtils.ExportService -> true))
    context.registerService(classOf[ViewerPageDataFacility], new ViewerPageDataFacilityImpl, dictionary)
  }
  def stop(context:BundleContext) {
    println("------------------------------------")
    println("Viewer Bundle Activator impl stopped")
    println("------------------------------------")
  }
}
