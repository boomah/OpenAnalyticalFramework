package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent._
import javafx.scene.input.KeyCode._

class OSXShortCutKeys extends GenericShortCutKeys {
  def pageBackOSSpecific = shortcut(LEFT)
  def pageForward = shortcut(RIGHT)
  def nextTab = shortcutShift(CLOSE_BRACKET)
  def previousTab = shortcutShift(OPEN_BRACKET)
}
