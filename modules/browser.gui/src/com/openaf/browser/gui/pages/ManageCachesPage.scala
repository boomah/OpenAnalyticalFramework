package com.openaf.browser.gui.pages

import com.openaf.browser.gui.api.PageFactory

case object ManageCachesPage extends BrowserPage

object ManageCachesPageFactory extends PageFactory {
  def page = ManageCachesPage
}