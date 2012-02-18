package com.openaf.test

import api.TestService
import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.utils.Utils

class TestBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("")
    println("-- Starting test")
    println("")

    context.registerService(classOf[TestService], new TestServiceImpl, null)
  }
  def stop(context:BundleContext) {}
}

class TestServiceImpl extends TestService {
//  def message:String = "HELLO NICK"
  def message:String = Utils.UtilsString
}
