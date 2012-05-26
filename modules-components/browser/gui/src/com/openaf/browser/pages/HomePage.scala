package com.openaf.browser.pages

import com.openaf.browser.utils.BrowserUtils
import com.openaf.browser.{PageData, Page}

case object HomePage extends Page {
  def name = "OpenAF"
  def image = BrowserUtils.icon("16x16_home.png")
  def build = {
    println("^^^ Building home page - sleep")
    Thread.sleep(5000)
    PageData.NoPageData
  }
}
