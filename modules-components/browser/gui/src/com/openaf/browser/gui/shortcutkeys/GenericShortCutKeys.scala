package com.openaf.browser.gui.shortcutkeys

import javafx.scene.input.{KeyEvent, KeyCombination}

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

class EnhancedKeyEvent(private val names:String*) {
  private val keyCombinations = names.map(name => KeyCombination.keyCombination(name))
  assert(keyCombinations.nonEmpty)
  def matches(e:KeyEvent) = keyCombinations.exists(keyCombination =>keyCombination.`match`(e))
  def accelerator = keyCombinations.head
  def +(enhancedKeyEvent:EnhancedKeyEvent) = new EnhancedKeyEvent((names ++ enhancedKeyEvent.names).toArray:_*)
}

object EnhancedKeyEvent {
  val Null = new EnhancedKeyEvent("Null")
}
