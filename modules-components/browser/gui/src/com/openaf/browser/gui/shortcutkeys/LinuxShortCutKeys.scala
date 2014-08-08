package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent

class LinuxShortCutKeys extends GenericShortCutKeys {
  def pageBackOSSpecific = alt("Left")
  def pageForward = alt("Right")
  def nextTab = EnhancedKeyEvent.Null
  def previousTab = EnhancedKeyEvent.Null
}
