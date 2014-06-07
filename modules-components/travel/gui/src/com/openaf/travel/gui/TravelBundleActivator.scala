package com.openaf.travel.gui

import components.HotelsPageComponentFactory
import org.osgi.framework.{BundleActivator, BundleContext}
import com.openaf.travel.api.{HotelsPage, FlightsAndHotelsPageFactory, HotelsPageFactory}
import com.openaf.table.gui.OpenAFTable
import com.openaf.browser.gui.api.{PageContext, BrowserActionButton, OpenAFApplication}

class TravelBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("TravelBundleActivator gui started")
    context.registerService(classOf[OpenAFApplication], TravelBrowserApplication, null)
  }
  def stop(context:BundleContext) {
    println("TravelBundleActivator gui stopped")
  }
}

object TravelBrowserApplication extends OpenAFApplication {
  override def applicationButtons(context:PageContext) = {
    List(
      BrowserActionButton("Hotels", HotelsPageFactory),
      BrowserActionButton("Flights and Hotels", FlightsAndHotelsPageFactory)
    )
  }
  override def componentFactoryMap = Map(classOf[HotelsPage].getName -> HotelsPageComponentFactory)
  override def styleSheets = OpenAFTable.styleSheets
}