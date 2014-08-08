package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent._

class LinuxShortCutKeys extends GenericShortCutKeys {
  def pageBackOSSpecific = alt("Left")
  def pageForward = alt("Right")
  def nextTab = Null
  def previousTab = Null
}
