package com.openaf.browser.pages

import com.openaf.browser.Page
import com.openaf.browser.utils.BrowserUtils

object HomePage extends Page {
  def name = "OpenAF"
  def image = BrowserUtils.icon("16x16_home.png")
}
