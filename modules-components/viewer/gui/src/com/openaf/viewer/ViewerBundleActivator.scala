package com.openaf.viewer

import components.ViewPageComponentFactory
import org.osgi.framework.BundleContext
import com.openaf.browser.{BrowserApplicationButton, PageContext, BrowserApplication}
import com.openaf.osgi.OpenAFBundleActivator

class ViewerBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    println("Viewer Started!!")
    context.registerService(classOf[BrowserApplication], ViewerBrowserApplication, null)
  }
  def stop(context:BundleContext) {}
}

object ViewerBrowserApplication extends BrowserApplication {
  def applicationName = "Viewer"
  override def browserApplicationButtons(context:PageContext) = List(BrowserApplicationButton("View", ViewerPageFactory))
  override def componentFactoryMap = Map(ViewPage.name -> ViewPageComponentFactory)
}