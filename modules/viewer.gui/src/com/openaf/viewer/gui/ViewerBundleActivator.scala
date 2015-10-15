package com.openaf.viewer.gui

import org.osgi.framework.{BundleActivator, BundleContext}
import com.openaf.viewer.gui.components.ViewerPageComponentFactory
import com.openaf.viewer.api.ViewerPage
import com.openaf.browser.gui.api.{PageFactory, BrowserContext, BrowserActionButton, OpenAFApplication}

class ViewerBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("GUI Viewer Started!!")
    context.registerService(classOf[OpenAFApplication], ViewerBrowserApplication, null)
  }
  def stop(context:BundleContext) {
    println("GUI Viewer Stopped!!")
  }
}

object ViewerBrowserApplication extends OpenAFApplication {
  override def applicationButtons(context:BrowserContext) = List(BrowserActionButton("View", ViewerPageFactory))
  override def componentFactoryMap = Map(classOf[ViewerPage].getName -> ViewerPageComponentFactory)
}

object ViewerPageFactory extends PageFactory {
  def page = ViewerPage(0)
}