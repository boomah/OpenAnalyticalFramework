package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.api.Field
import javafx.scene.Node
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import collection.mutable
import javafx.beans.value.{ObservableValue, ChangeListener}

class DragAndDrop {
  val fieldsBeingDragged = new SimpleObjectProperty[List[Field]](Nil)
  private val dropTargetContainers = new mutable.HashSet[DropTargetContainer]
  def register(dropTargetContainer:DropTargetContainer) {dropTargetContainers += dropTargetContainer}

  fieldsBeingDragged.addListener(new ChangeListener[List[Field]] {
    def changed(observable:ObservableValue[_<:List[Field]], oldValue:List[Field], newValue:List[Field]) {
      println("Dragging Changed - " + (oldValue, newValue, fieldsBeingDragged.get))
    }
  })

  private def hypot(x1:Double, y1:Double, x2:Double, y2:Double) = math.hypot(x2 - x1, y2 - y1)

  private def closestDropTarget(mouseSceneX:Double, mouseSceneY:Double) = {
    val fields = fieldsBeingDragged.get
    val dropTargets = dropTargetContainers.flatMap(_.dropTargets(fields))
    dropTargets.minBy(dropTarget => {
      val sceneBounds = dropTarget.localToScene(dropTarget.getBoundsInLocal)

      val (leftX, rightX, topY, bottomY) = (sceneBounds.getMinX, sceneBounds.getMaxX, sceneBounds.getMinY, sceneBounds.getMaxY)

      if (sceneBounds.contains(mouseSceneX, mouseSceneY)) {
        0
      } else {
        val distanceToMouse = hypot(mouseSceneX, mouseSceneY, _:Double, _:Double)
        if (mouseSceneY < topY) {
          if (mouseSceneX < leftX) {
            distanceToMouse(leftX, topY)
          } else if (mouseSceneX > rightX) {
            distanceToMouse(rightX, topY)
          } else {
            topY - mouseSceneY
          }
        } else if (mouseSceneY > bottomY) {
          if (mouseSceneX < leftX) {
            distanceToMouse(leftX, bottomY)
          } else if (mouseSceneX > rightX) {
            distanceToMouse(rightX, bottomY)
          } else {
            mouseSceneY - bottomY
          }
        } else if (mouseSceneX < leftX) {
          leftX - mouseSceneX
        } else {
          mouseSceneX - rightX
        }
      }
    })
  }

  def updateClosestDropTarget(mouseSceneX:Double, mouseSceneY:Double) {
    val dropTarget = closestDropTarget(mouseSceneX, mouseSceneY)
    println("Closest Drop Target : " + dropTarget)
  }
}

case class FieldDragInfo(field:Field)

trait Draggable extends Node {
  def dragAndDrop:DragAndDrop
  def fields:List[Field]

  setOnDragDetected(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      dragAndDrop.fieldsBeingDragged.set(fields)
      dragAndDrop.updateClosestDropTarget(event.getSceneX, event.getSceneY)
      event.consume()
    }
  })

  setOnMouseDragged(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      dragAndDrop.updateClosestDropTarget(event.getSceneX, event.getSceneY)
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

trait DropTarget extends Node

trait DropTargetContainer {
  def dropTargets(draggedFields:List[Field]):List[DropTarget]
}