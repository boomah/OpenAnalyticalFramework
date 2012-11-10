package com.openaf.pagemanager

import com.openaf.osgi.OpenAFBundleActivator
import org.osgi.framework.BundleContext

class PageManagerBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    println("Starling PageManagerBundleActivator although it doesn't do anything at the moment")
  }
  def stop(context:BundleContext) {}
}
