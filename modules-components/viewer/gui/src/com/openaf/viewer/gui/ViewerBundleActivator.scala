package com.openaf.viewer.gui

import org.osgi.framework.BundleContext
import com.openaf.browser.gui.{BrowserActionButton, PageContext, OpenAFApplication}
import com.openaf.osgi.OpenAFBundleActivator
import com.openaf.viewer.gui.components.ViewPageComponentFactory

class ViewerBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    println("Viewer Started!!")
    context.registerService(classOf[OpenAFApplication], ViewerBrowserApplication, null)
  }
  def stop(context:BundleContext) {}
}

object ViewerBrowserApplication extends OpenAFApplication {
  def applicationName = "Viewer"
  override def applicationButtons(context:PageContext) = List(BrowserActionButton("View", ViewerPageFactory))
  override def componentFactoryMap = Map(classOf[ViewPage].getName -> ViewPageComponentFactory)
}