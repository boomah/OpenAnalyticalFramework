package com.openaf.browser.gui

import components.ManageCachesPageComponentFactory
import pages.{ManageCachesPage, ManageCachesPageFactory}

object BrowserApplication extends OpenAFApplication {
  def applicationName = "Browser"
  override def utilButtons(context:PageContext) = List(BrowserActionButton("Manage Caches", ManageCachesPageFactory))
  override def componentFactoryMap = Map(ManageCachesPage.getClass.getName -> ManageCachesPageComponentFactory)
}
