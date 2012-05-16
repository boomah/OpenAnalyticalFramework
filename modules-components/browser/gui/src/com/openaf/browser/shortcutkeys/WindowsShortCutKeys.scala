package com.openaf.browser.shortcutkeys

class WindowsShortCutKeys extends GenericShortCutKeys {
  def pageBack = alt("Left")
  def pageForward = alt("Right")
  def nextTab = EnhancedKeyEvent.Null
  def previousTab = EnhancedKeyEvent.Null
}
