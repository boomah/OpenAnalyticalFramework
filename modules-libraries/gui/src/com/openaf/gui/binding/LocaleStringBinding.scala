package com.openaf.gui.binding

import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import javafx.beans.binding.StringBinding

abstract class LocaleStringBinding(locale:SimpleObjectProperty[Locale]) extends StringBinding {
  bind(locale)
}