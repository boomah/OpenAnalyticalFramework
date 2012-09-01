package com.openaf.browser.gui.pages

import com.openaf.browser.gui.{PageData, Page}
import com.openaf.browser.gui.utils.BrowserUtils

case object HomePage extends Page {
  def name = "OpenAF"
  def image = BrowserUtils.icon("16x16_home.png")
  def build = {
    println("^^^ Building home page - sleep")
    Thread.sleep(5000)
    PageData.NoPageData
  }
}
