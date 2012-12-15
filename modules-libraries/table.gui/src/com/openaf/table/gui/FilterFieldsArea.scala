package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label

class FilterFieldsArea extends StackPane {
  private val descriptionLabel = new Label("Drop Filter Fields Here")
  getChildren.add(descriptionLabel)
}
