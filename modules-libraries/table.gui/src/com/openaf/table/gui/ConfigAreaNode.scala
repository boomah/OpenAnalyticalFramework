package com.openaf.table.gui

import javafx.scene.Node

trait ConfigAreaNode extends Node {
  def setDefaultFocus()
  def isConfigAreaNodeFocused:Boolean
}
