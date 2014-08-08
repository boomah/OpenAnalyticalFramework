package com.openaf.gui.utils

import javafx.scene.input.{KeyEvent, KeyCombination}

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
