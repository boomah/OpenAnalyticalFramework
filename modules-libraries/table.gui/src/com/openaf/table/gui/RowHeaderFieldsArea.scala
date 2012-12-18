package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label
import javafx.event.EventHandler
import javafx.scene.input.DragEvent

class RowHeaderFieldsArea extends StackPane {
  private val descriptionLabel = new Label("Drop Row Header Fields Here")
  getChildren.add(descriptionLabel)

  setOnDragEntered(new EventHandler[DragEvent] {
    def handle(event:DragEvent) {
      if (event.getDragboard.hasString) {
        val fieldID = event.getDragboard.getString
        println("Entered Row Header Area with " + fieldID)
      }
      event.consume()
    }
  })
}
