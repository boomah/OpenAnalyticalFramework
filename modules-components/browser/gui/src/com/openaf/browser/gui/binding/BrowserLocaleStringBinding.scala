package com.openaf.browser.gui.binding

import com.openaf.browser.gui.api.BrowserCache
import java.util.ResourceBundle
import com.openaf.browser.gui.api.binding.LocaleStringBinding

class BrowserLocaleStringBinding(id:String, cache:BrowserCache) extends LocaleStringBinding(cache) {
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.browser.gui.resources.browser", localeProperty.get)
    bundle.getString(id)
  }
}
