package com.openaf.browser.gui.binding

import com.openaf.browser.gui.api.{BrowserCache, BrowserCacheKey, OpenAFApplication}
import javafx.beans.binding.StringBinding
import java.util.ResourceBundle

class ApplicationLocaleStringBinding(id:String, application:OpenAFApplication, cache:BrowserCache) extends StringBinding {
  private val localeProperty = cache(BrowserCacheKey.LocaleKey)
  bind(localeProperty)
  def computeValue = {
    val bundle = ResourceBundle.getBundle(
      application.resourceLocation, localeProperty.getValue, application.getClass.getClassLoader
    )
    if (bundle.containsKey(id)) bundle.getString(id) else id
  }
}
