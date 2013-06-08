package com.openaf.table.gui.binding

import com.openaf.gui.binding.LocaleStringBinding
import javafx.beans.property.SimpleObjectProperty
import java.util.{ResourceBundle, Locale}

class TableLocaleStringBinding(locale:SimpleObjectProperty[Locale], id:String) extends LocaleStringBinding(locale) {
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.table.gui.resources.table", locale.get)
    bundle.getString(id)
  }
}
