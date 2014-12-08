package com.openaf.pagemanager

import org.osgi.framework.{BundleActivator, BundleContext}

class PageManagerBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("Starling PageManagerBundleActivator although it doesn't do anything at the moment")
  }
  def stop(context:BundleContext) {}
}
