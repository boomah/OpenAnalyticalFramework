package com.openaf.table.gui.binding

import javafx.beans.property.Property
import java.util.{ResourceBundle, Locale}
import javafx.beans.binding.StringBinding

class TableLocaleStringBinding(id:String, locale:Property[Locale],
                               prefix:Option[String]=None) extends StringBinding {
  bind(locale)
  def computeValue = {
    val bundle = ResourceBundle.getBundle("com.openaf.table.gui.resources.table", locale.getValue)
    prefix.getOrElse("") + bundle.getString(id)
  }
}
