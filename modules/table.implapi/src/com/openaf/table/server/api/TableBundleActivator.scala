package com.openaf.table.server.api

import com.openaf.osgi.OSGIUtils
import com.openaf.table.api.TablePageDataFacility
import org.osgi.framework.{BundleContext, BundleActivator}

trait TableBundleActivator extends BundleActivator {
  def tablePageDataFacility:TablePageDataFacility

  def start(context:BundleContext) {
    println(s"${tablePageDataFacility.nameId} started")
    val dictionary = OSGIUtils.mapToDictionary(Map(OSGIUtils.ExportService -> true))
    context.registerService(classOf[TablePageDataFacility], tablePageDataFacility, dictionary)
  }
  def stop(context:BundleContext) {
    println(s"${tablePageDataFacility.nameId} stopped")
  }
}
