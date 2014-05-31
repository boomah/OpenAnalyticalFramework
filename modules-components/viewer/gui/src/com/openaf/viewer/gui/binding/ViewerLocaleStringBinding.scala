package com.openaf.viewer.gui.binding

import java.util.ResourceBundle
import com.openaf.browser.gui.api.binding.LocaleStringBinding
import com.openaf.browser.gui.api.BrowserCache

class ViewerLocaleStringBinding(id:String, cache:BrowserCache) extends LocaleStringBinding(cache) {
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.viewer.gui.resources.viewer", localeProperty.get)
    bundle.getString(id)
  }
}
