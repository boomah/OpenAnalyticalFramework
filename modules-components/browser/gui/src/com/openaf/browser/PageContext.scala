package com.openaf.browser

class PageContext(val browserCache:BrowserCache, browser:Browser) {
  def goToPage(page:Page) {
    browser.goToPage(page)
  }
}
