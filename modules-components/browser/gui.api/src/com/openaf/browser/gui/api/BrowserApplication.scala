package com.openaf.browser.gui.api

import com.openaf.browser.gui.api.pages.{ManageCachesPage, ManageCachesPageFactory}
import com.openaf.browser.gui.api.components.ManageCachesPageComponentFactory

object BrowserApplication extends OpenAFApplication {
  def applicationName = "Browser"
  override def utilButtons(context:PageContext) = List(BrowserActionButton("Manage Caches", ManageCachesPageFactory))
  override def componentFactoryMap = Map(ManageCachesPage.getClass.getName -> ManageCachesPageComponentFactory)
}
