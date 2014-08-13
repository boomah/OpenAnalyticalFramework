package com.openaf.browser.gui.shortcutkeys

import com.openaf.gui.utils.EnhancedKeyEvent,EnhancedKeyEvent._
import javafx.scene.input.KeyCode._

abstract class GenericShortCutKeys {
  def exitApplication = shortcut(Q)
  def newWindow = shortcut(N)
  def newTab = shortcut(T)
  def closeTab = shortcut(W)
  def pageBack = keyEvent(BACK_SPACE) + pageBackOSSpecific
  def pageBackOSSpecific:EnhancedKeyEvent
  def pageBackBack = shortcutAlt(LEFT)
  def pageForward:EnhancedKeyEvent
  def pageForwardForward = shortcutAlt(RIGHT)
  def nextTab:EnhancedKeyEvent
  def previousTab:EnhancedKeyEvent
  def utilsPage = shortcutShift(U)
}
