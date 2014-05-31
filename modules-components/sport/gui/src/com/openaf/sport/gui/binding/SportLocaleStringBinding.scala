package com.openaf.sport.gui.binding

import com.openaf.browser.gui.api.BrowserCache
import com.openaf.browser.gui.api.binding.LocaleStringBinding
import java.util.ResourceBundle

class SportLocaleStringBinding(id:String, cache:BrowserCache) extends LocaleStringBinding(cache) {
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.sport.gui.resources.sport", localeProperty.get)
    bundle.getString(id)
  }
}
