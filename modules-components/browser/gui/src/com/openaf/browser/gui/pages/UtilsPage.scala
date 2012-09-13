package com.openaf.browser.gui.pages

import com.openaf.browser.gui.{NoPageData, BrowserPage}
import com.openaf.browser.gui.utils.BrowserUtils

object UtilsPage extends BrowserPage {
  def name = "Utils"
  def image = BrowserUtils.icon("16x16_home.png")
  def build(serverContext:String) = NoPageData
}
