package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent._
import javafx.scene.input.KeyCode._

class WindowsShortCutKeys extends GenericShortCutKeys {
  def pageBackOSSpecific = alt(LEFT)
  def pageForward = alt(RIGHT)
  def nextTab = Null
  def previousTab = Null
}
