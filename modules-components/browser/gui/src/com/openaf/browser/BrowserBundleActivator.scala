package com.openaf.browser

import org.osgi.framework.{BundleContext, BundleActivator}

class BrowserBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("Starting Browser")
  }

  def stop(context:BundleContext) {}
}
