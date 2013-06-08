package com.openaf.browser.gui.api.binding

import javafx.beans.property.SimpleObjectProperty
import java.util.{ResourceBundle, Locale}
import com.openaf.gui.binding.LocaleStringBinding

class BrowserLocaleStringBinding(locale:SimpleObjectProperty[Locale], id:String) extends LocaleStringBinding(locale) {
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.browser.gui.api.resources.browser", locale.get)
    bundle.getString(id)
  }
}
