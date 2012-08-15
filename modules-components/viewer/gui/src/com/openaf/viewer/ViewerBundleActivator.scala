package com.openaf.viewer

import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.browser.{BrowserApplicationButton, PageContext, BrowserApplication}

class ViewerBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("Viewer Started!!")
    context.registerService(classOf[BrowserApplication], ViewerBrowserApplication, null)
  }
  def stop(context:BundleContext) {}
}

object ViewerBrowserApplication extends BrowserApplication {
  def applicationName = "Viewer"
  override def homePageButtons(context:PageContext) = List(BrowserApplicationButton("Viewer"))
}