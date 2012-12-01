package com.openaf.travel.gui

import com.openaf.osgi.OpenAFBundleActivator
import org.osgi.framework.BundleContext
import com.openaf.browser.gui.{BrowserActionButton, PageContext, OpenAFApplication}
import com.openaf.browser.gui.pages.{BlankPage, BlankPageFactory}
import com.openaf.browser.gui.components.BlankPageComponentFactory

class TravelBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    println("TravelBundleActivator gui started")
    context.registerService(classOf[OpenAFApplication], TravelBrowserApplication, null)
  }
  def stop(context:BundleContext) {
    println("TravelBundleActivator gui stopped")
  }
}

object TravelBrowserApplication extends OpenAFApplication {
  def applicationName = "Travel"
  override def applicationButtons(context:PageContext) = {
    List(
      BrowserActionButton("Hotels", BlankPageFactory),
      BrowserActionButton("Flights and Hotels", BlankPageFactory)
    )
  }
  override def componentFactoryMap = Map(BlankPage.getClass.getName -> BlankPageComponentFactory)
}