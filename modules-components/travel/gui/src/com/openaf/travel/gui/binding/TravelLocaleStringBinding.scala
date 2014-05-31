package com.openaf.travel.gui.binding

import com.openaf.browser.gui.api.BrowserCache
import com.openaf.browser.gui.api.binding.LocaleStringBinding
import java.util.ResourceBundle

class TravelLocaleStringBinding(id:String, cache:BrowserCache) extends LocaleStringBinding(cache) {
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.travel.gui.resources.travel", localeProperty.get)
    bundle.getString(id)
  }
}
