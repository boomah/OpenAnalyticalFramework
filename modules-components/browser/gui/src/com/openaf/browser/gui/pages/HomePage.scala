package com.openaf.browser.gui.pages

import com.openaf.browser.gui.{NoPageData, BrowserPage}
import com.openaf.browser.gui.utils.BrowserUtils

case object HomePage extends BrowserPage {
  def name = "OpenAF"
  def image = BrowserUtils.icon("16x16_home.png")
  def build(serverContext:String) = NoPageData
}
