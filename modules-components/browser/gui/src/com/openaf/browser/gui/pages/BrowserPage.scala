package com.openaf.browser.gui.pages

import com.openaf.pagemanager.api._

trait BrowserPage extends Page {
  def pageDataFacility(serverContext:ServerContext) = BrowserPageDataFacility
}

object BrowserPageDataFacility extends PageDataFacility {
  def pageData(page:Page) = NoPageData
}
