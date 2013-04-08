package com.openaf.browser.gui.api.pages

import com.openaf.pagemanager.api.PageFactory

case object ManageCachesPage extends BrowserPage

object ManageCachesPageFactory extends PageFactory {
  def page = ManageCachesPage
}