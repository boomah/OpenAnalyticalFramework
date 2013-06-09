package com.openaf.browser.gui

import com.openaf.browser.gui.pages.{ManageCachesPage, ManageCachesPageFactory}
import com.openaf.browser.gui.components.ManageCachesPageComponentFactory
import com.openaf.browser.gui.api.{BrowserActionButton, PageContext, OpenAFApplication}

object BrowserApplication extends OpenAFApplication {
  def applicationName = "Browser"
  override def utilButtons(context:PageContext) = List(BrowserActionButton("Manage Caches", ManageCachesPageFactory))
  override def componentFactoryMap = Map(ManageCachesPage.getClass.getName -> ManageCachesPageComponentFactory)
}
