package com.openaf.gui.utils

import javafx.scene.input.{KeyCombination, KeyCodeCombination, KeyCode, KeyEvent},KeyCombination._
import javafx.scene.input.KeyCombination.Modifier

class EnhancedKeyEvent(private val keyCombinations:KeyCombination*) {
  assert(keyCombinations.nonEmpty)
  def matches(e:KeyEvent) = keyCombinations.exists(keyCodeCombination =>keyCodeCombination.`match`(e))
  def accelerator = keyCombinations.head
  def +(enhancedKeyEvent:EnhancedKeyEvent) = {
    new EnhancedKeyEvent((keyCombinations ++ enhancedKeyEvent.keyCombinations).toArray:_*)
  }
}

object EnhancedKeyEvent {
  val Null = new EnhancedKeyEvent(KeyCombination.keyCombination("Null"))

  def keyEvent(keyCode:KeyCode, modifiers:Modifier*) = new EnhancedKeyEvent(new KeyCodeCombination(keyCode, modifiers:_*))
  def shortcut(keyCode:KeyCode) = keyEvent(keyCode, SHORTCUT_DOWN)
  def alt(keyCode:KeyCode) = keyEvent(keyCode, ALT_DOWN)
  def shortcutAlt(keyCode:KeyCode) = keyEvent(keyCode, SHORTCUT_DOWN, ALT_DOWN)
  def shortcutShift(keyCode:KeyCode) = keyEvent(keyCode, SHORTCUT_DOWN, SHIFT_DOWN)
}
