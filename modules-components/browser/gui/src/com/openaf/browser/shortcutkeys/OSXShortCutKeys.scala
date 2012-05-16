package com.openaf.browser.shortcutkeys

class OSXShortCutKeys extends GenericShortCutKeys {
  def pageBack = shortCut("Left")
  def pageForward = shortCut("Right")
  def nextTab = shortCutAlt("Right")
  def previousTab = shortCutAlt("Left")
}
