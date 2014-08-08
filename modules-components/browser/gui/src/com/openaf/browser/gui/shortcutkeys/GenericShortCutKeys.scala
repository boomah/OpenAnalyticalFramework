package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent

abstract class GenericShortCutKeys {
  protected def keyEvent(name:String) = new EnhancedKeyEvent(name)
  protected def shortCut(key:String) = keyEvent("Shortcut+" + key)
  protected def alt(key:String) = keyEvent("Alt+" + key)
  protected def shortCutAlt(key:String) = keyEvent("Shortcut+Alt+" + key)
  protected def shortCutShift(key:String) = keyEvent("Shortcut+Shift+" + key)

  def exitApplication = shortCut("Q")
  def newWindow = shortCut("N")
  def newTab = shortCut("T")
  def closeTab = shortCut("W")
  def pageBack = keyEvent("BackSpace") + pageBackOSSpecific
  def pageBackOSSpecific:EnhancedKeyEvent
  def pageBackBack = shortCutAlt("Left")
  def pageForward:EnhancedKeyEvent
  def pageForwardForward = shortCutAlt("Right")
  def nextTab:EnhancedKeyEvent
  def previousTab:EnhancedKeyEvent
  def utilsPage = shortCutShift("U")
}
