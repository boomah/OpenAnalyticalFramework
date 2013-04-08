package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.Page

class PageContext(val browserCache:BrowserCache, browser:Browser) {
  def goToPage(page:Page) {browser.goToPage(page)}
}
