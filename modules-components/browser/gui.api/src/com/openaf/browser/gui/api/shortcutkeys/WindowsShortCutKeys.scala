package com.openaf.browser.gui.api.shortcutkeys

class WindowsShortCutKeys extends GenericShortCutKeys {
  def pageBackOSSpecific = alt("Left")
  def pageForward = alt("Right")
  def nextTab = EnhancedKeyEvent.Null
  def previousTab = EnhancedKeyEvent.Null
}
