package com.openaf.sport.gui

import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.browser.gui.api.{BrowserActionButton, PageContext, OpenAFApplication}
import com.openaf.table.gui.OpenAFTable
import com.openaf.sport.gui.components.GoalsPageComponentFactory
import com.openaf.sport.api.{GoalsPageFactory, GoalsPage}

class SportBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("SportBundleActivator gui started")
    context.registerService(classOf[OpenAFApplication], SportBrowserApplication, null)
  }
  def stop(context:BundleContext) {
    println("SportBundleActivator gui stopped")
  }
}

object SportBrowserApplication extends OpenAFApplication {
  override def applicationButtons(context:PageContext) = {
    List(BrowserActionButton("Goals", GoalsPageFactory))
  }
  override def componentFactoryMap = Map(classOf[GoalsPage].getName -> GoalsPageComponentFactory)
  override def styleSheets = OpenAFTable.styleSheets
}