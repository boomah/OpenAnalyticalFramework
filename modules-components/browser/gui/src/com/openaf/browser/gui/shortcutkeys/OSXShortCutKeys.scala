package com.openaf.browser.gui.shortcutkeys

class OSXShortCutKeys extends GenericShortCutKeys {
  def pageBackOSSpecific = shortCut("Left")
  def pageForward = shortCut("Right")
  def nextTab = shortCutShift("Close Bracket")
  def previousTab = shortCutShift("Open Bracket")
}
