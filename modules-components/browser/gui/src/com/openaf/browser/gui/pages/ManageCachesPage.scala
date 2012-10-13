package com.openaf.browser.gui.pages

import com.openaf.browser.gui.{Page, PageFactory, NoPageData, BrowserPage}
import com.openaf.browser.gui.utils.BrowserUtils

case object ManageCachesPage extends BrowserPage {
  def name = "Manage Caches"
  def image = BrowserUtils.icon("16x16_home.png")
  def build(serverContext:String) = NoPageData
}

object ManageCachesPageFactory extends PageFactory {
  def page = ManageCachesPage
}