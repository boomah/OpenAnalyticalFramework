package com.openaf.utils

import org.osgi.framework.{BundleContext, BundleActivator}

class UtilsBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("")
    println("-- Utils started --")
    println("")
  }

  def stop(context:BundleContext) {}
}
