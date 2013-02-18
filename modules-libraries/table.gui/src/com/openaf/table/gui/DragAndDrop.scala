package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.api.Field
import javafx.scene.Node
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

class DragAndDrop {
  val fieldsBeingDragged = new SimpleObjectProperty[List[Field]](Nil)
}

case class FieldDragInfo(field:Field)

trait Draggable extends Node {
  def dragAndDrop:DragAndDrop
  def fields:List[Field]

  setOnDragDetected(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      event.getX
      dragAndDrop.fieldsBeingDragged.set(fields)
      event.consume()
    }
  })

  setOnMouseDragged(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
//      println("Mouse dragged " + (event.getSceneX, event.getSceneY))
      event.consume()
    }
  })

  setOnMouseReleased(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      dragAndDrop.fieldsBeingDragged.set(Nil)
      event.consume()
    }
  })
}