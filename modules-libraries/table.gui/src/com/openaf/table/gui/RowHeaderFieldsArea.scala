package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label
import javafx.event.EventHandler
import javafx.scene.input.DragEvent
import java.lang.{Boolean => JBoolean}
import javafx.beans.value.{ObservableValue, ChangeListener}

class RowHeaderFieldsArea(dragAndDrop:DragAndDrop) extends StackPane {
  private val descriptionLabel = new Label("Drop Row Header Fields Here")
  getChildren.add(descriptionLabel)

  dragAndDrop.dragging.addListener(new ChangeListener[JBoolean] {
    def changed(observable:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
      println("Dragging Changed - " + (oldValue, newValue, dragAndDrop.fieldBeingDragged.get))
    }
  })

  setOnDragEntered(new EventHandler[DragEvent] {
    def handle(event:DragEvent) {
      println("DRAG ENTERED")
      event.consume()
    }
  })
}
