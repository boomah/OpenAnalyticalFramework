package com.openaf.browser.gui

class PageContext(val browserCache:BrowserCache, browser:Browser) {
  def goToPage(page:Page) {
    browser.goToPage(page)
  }
}
