package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent,EnhancedKeyEvent._

abstract class GenericShortCutKeys {
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
