package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label

class AllFieldsArea extends StackPane {
  val label = new Label("All Fields")
  getChildren.addAll(label)
}
