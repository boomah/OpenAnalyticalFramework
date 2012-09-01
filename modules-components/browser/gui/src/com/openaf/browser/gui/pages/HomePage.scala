package com.openaf.browser.gui.pages

import com.openaf.browser.gui.{BrowserPage, PageData}
import com.openaf.browser.gui.utils.BrowserUtils

case object HomePage extends BrowserPage {
  def name = "OpenAF"
  def image = BrowserUtils.icon("16x16_home.png")
  def build(serverContext:String) = {
    println("^^^ Building home page - sleep")
    Thread.sleep(5000)
    PageData.NoPageData
  }
}
