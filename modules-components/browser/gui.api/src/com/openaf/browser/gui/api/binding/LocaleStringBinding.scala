package com.openaf.browser.gui.api.binding

import javafx.beans.binding.StringBinding
import com.openaf.browser.gui.api.{BrowserCacheKey, BrowserCache}

abstract class LocaleStringBinding(cache:BrowserCache) extends StringBinding {
  protected val localeProperty = cache(BrowserCacheKey.LocaleKey)
  bind(localeProperty)
}
