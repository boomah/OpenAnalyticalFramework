package com.openaf.test

import api.TestService
import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.utils.Utils
import com.openaf.osgi.OSGIUtils

class TestBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("--- Test bundle activator")
    val dictionary = OSGIUtils.mapToDictionary(Map(OSGIUtils.ExportService -> true))
    context.registerService(classOf[TestService], new TestServiceImpl, dictionary)
  }
  def stop(context:BundleContext) {}
}

class TestServiceImpl extends TestService {
  def message:String = Utils.UtilsString
  def message2(text:String) = "Hello " + text
  def message3(say:Boolean, text:String) = "Should I say something? " + say + " ok, " + text
}
