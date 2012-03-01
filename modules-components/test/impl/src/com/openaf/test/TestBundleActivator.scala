package com.openaf.test

import api.TestService
import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.utils.Utils

class TestBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    context.registerService(classOf[TestService], new TestServiceImpl, null)
  }
  def stop(context:BundleContext) {}
}

class TestServiceImpl extends TestService {
  def message:String = Utils.UtilsString
}
