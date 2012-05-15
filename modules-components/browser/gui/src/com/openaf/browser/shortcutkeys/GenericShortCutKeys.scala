package com.openaf.browser.shortcutkeys

import javafx.scene.input.{KeyEvent, KeyCombination}


abstract class GenericShortCutKeys {
  protected def keyEvent(name:String) = new EnhancedKeyEvent(name)
  protected def shortCut(key:String) = keyEvent("Shortcut+" + key)
  protected def alt(key:String) = keyEvent("Alt+" + key)

  def exitApplication = shortCut("Q")
  def newWindow = shortCut("N")
  def newTab = shortCut("T")
  def closeTab = shortCut("W")
  def undo = shortCut("Z")
  def pageBack:EnhancedKeyEvent
  def redo = shortCut("Shift+Z")
  def pageForward:EnhancedKeyEvent
}

class EnhancedKeyEvent(name:String) {
  private val keyCombination = KeyCombination.keyCombination(name)
  def matches(e:KeyEvent) = keyCombination.`match`(e)
  def accelerator = keyCombination
}
