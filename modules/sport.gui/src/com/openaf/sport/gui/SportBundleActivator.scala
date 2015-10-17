package com.openaf.sport.gui

import com.openaf.table.lib.api.TableState
import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.browser.gui.api.{PageFactory, BrowserActionButton, BrowserContext, OpenAFApplication}
import com.openaf.table.gui.OpenAFTable
import com.openaf.sport.gui.components.GoalsPageComponentFactory
import com.openaf.sport.api.GoalsPage

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
  override def applicationButtons(context:BrowserContext) = {
    List(BrowserActionButton(GoalsPageComponentFactory.pageComponent.nameId, GoalsPageFactory))
  }
  override def componentFactoryMap = Map(classOf[GoalsPage].getName -> GoalsPageComponentFactory)
  override def styleSheets = OpenAFTable.styleSheets
  override def order = -1 // For now I want the sport application to be first
}

object GoalsPageFactory extends PageFactory {
  def page = GoalsPage(TableState.Blank)
}