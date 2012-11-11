package com.openaf.viewer

import api.ViewerPageDataFacility
import com.openaf.osgi.{OSGIUtils, OpenAFBundleActivator}
import org.osgi.framework.BundleContext

class ViewerBundleActivator extends OpenAFBundleActivator {
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
