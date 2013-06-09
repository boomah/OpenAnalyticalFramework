package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.Page

trait PageContext {
  def browserCache:BrowserCache
  def goToPage(page:Page)
}
