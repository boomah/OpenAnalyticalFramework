package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label
import javafx.event.EventHandler
import javafx.scene.input.DragEvent
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.api.Field

class RowHeaderFieldsArea(val dragAndDrop:DragAndDrop) extends StackPane {
  private val descriptionLabel = new Label("Drop Row Header Fields Here")
  getChildren.add(descriptionLabel)

  dragAndDrop.fieldsBeingDragged.addListener(new ChangeListener[List[Field]] {
    def changed(observable:ObservableValue[_<:List[Field]], oldValue:List[Field], newValue:List[Field]) {
      println("Dragging Changed - " + (oldValue, newValue, dragAndDrop.fieldsBeingDragged.get))
    }
  })

  setOnDragEntered(new EventHandler[DragEvent] {
    def handle(event:DragEvent) {
      println("DRAG ENTERED")
      event.consume()
    }
  })
}
