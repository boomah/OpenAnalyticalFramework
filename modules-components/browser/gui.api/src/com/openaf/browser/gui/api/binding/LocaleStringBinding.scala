package com.openaf.browser.gui.api.binding

import javafx.beans.binding.StringBinding
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale

abstract class LocaleStringBinding(locale:SimpleObjectProperty[Locale]) extends StringBinding {
  bind(locale)
}