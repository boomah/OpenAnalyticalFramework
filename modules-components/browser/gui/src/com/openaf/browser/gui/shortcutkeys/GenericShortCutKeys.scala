package com.openaf.browser.gui.shortcutkeys

import javafx.scene.input.{KeyEvent, KeyCombination}


abstract class GenericShortCutKeys {
  protected def keyEvent(name:String) = new EnhancedKeyEvent(name)
  protected def shortCut(key:String) = keyEvent("Shortcut+" + key)
  protected def alt(key:String) = keyEvent("Alt+" + key)
  protected def shortCutAlt(key:String) = keyEvent("Shortcut+Alt+" + key)

  def exitApplication = shortCut("Q")
  def newWindow = shortCut("N")
  def newTab = shortCut("T")
  def closeTab = shortCut("W")
  def undo = shortCut("Z")
  def pageBack:EnhancedKeyEvent
  def redo = shortCut("Shift+Z")
  def pageForward:EnhancedKeyEvent
  def nextTab:EnhancedKeyEvent
  def previousTab:EnhancedKeyEvent
}

class EnhancedKeyEvent(names:String*) {
  private val keyCombinations = names.map(name => KeyCombination.keyCombination(name))
  assert(keyCombinations.nonEmpty)
  def matches(e:KeyEvent) = keyCombinations.exists(keyCombination =>keyCombination.`match`(e))
  def accelerator = keyCombinations.head
}

object EnhancedKeyEvent {
  val Null = new EnhancedKeyEvent("Null")
}
