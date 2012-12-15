package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label

class RowHeaderFieldsArea extends StackPane {
  private val descriptionLabel = new Label("Drop Row Header Fields Here")
  getChildren.add(descriptionLabel)
}
