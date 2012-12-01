package com.openaf.travel

import com.openaf.osgi.OpenAFBundleActivator
import org.osgi.framework.BundleContext

class TravelBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    println("TravelBundleActivator started")
  }
  def stop(context:BundleContext) {
    println("TravelBundleActivator stopped")
  }
}
