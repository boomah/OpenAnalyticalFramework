package com.openaf.table.gui

import javafx.beans.property.{ReadOnlyStringProperty, SimpleObjectProperty}
import com.openaf.table.api.{TableData, Field}
import javafx.scene.Node
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}
import collection.mutable
import javafx.beans.value.{ObservableValue, ChangeListener}

class DragAndDrop {
  val fieldsBeingDraggedInfo = new SimpleObjectProperty[Option[DraggableFieldsInfo]](None)
  val closestDropTarget = new SimpleObjectProperty[Option[DropTarget]](None)
  private val dropTargetContainers = new mutable.HashSet[DropTargetContainer]
  def register(dropTargetContainer:DropTargetContainer) {dropTargetContainers += dropTargetContainer}

  closestDropTarget.addListener(new ChangeListener[Option[DropTarget]] {
    def changed(observableValue:ObservableValue[_<:Option[DropTarget]], oldDropTarget:Option[DropTarget], newDropTarget:Option[DropTarget]) {
      oldDropTarget match {
        case Some(dropTarget) => dropTarget.setId(null)
        case _ =>
      }
      newDropTarget match {
        case Some(dropTarget) => dropTarget.setId("closest-drop-target-node")
        case _ =>
      }
    }
  })

  private def hypot(x1:Double, y1:Double, x2:Double, y2:Double) = math.hypot(x2 - x1, y2 - y1)

  private def closestDropTarget(mouseSceneX:Double, mouseSceneY:Double) = {
    val draggableFieldsInfo = fieldsBeingDraggedInfo.get.get
    val dropTargets = dropTargetContainers.flatMap(_.dropTargets(draggableFieldsInfo))
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
    closestDropTarget.set(Some(dropTarget))
  }
}

trait Draggable extends Node {
  def dragAndDrop:DragAndDrop
  def draggableParent:DraggableParent
  def fields:List[Field]
  // When dropped here, nothing will happen. Usually just the Draggable itself, but in the case of a Draggable being
  // dragged from the AllFieldsArea, the AllFieldsArea scene bounds are used.
  def noOpSceneBounds = localToScene(getBoundsInLocal)
  val tableData:SimpleObjectProperty[TableData]

  private def updateClosestDropTarget(mouseSceneX:Double, mouseSceneY:Double) {
    if (!noOpSceneBounds.contains(mouseSceneX, mouseSceneY)) {
      dragAndDrop.updateClosestDropTarget(mouseSceneX, mouseSceneY)
    } else {
      dragAndDrop.closestDropTarget.set(None)
    }
  }

  setOnDragDetected(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY) {
        dragAndDrop.fieldsBeingDraggedInfo.set(Some(DraggableFieldsInfo(Draggable.this, draggableParent)))
        updateClosestDropTarget(event.getSceneX, event.getSceneY)
      }
      event.consume()
    }
  })

  setOnMouseDragged(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      dragAndDrop.fieldsBeingDraggedInfo.get.foreach(_ => updateClosestDropTarget(event.getSceneX, event.getSceneY))
      event.consume()
    }
  })

  setOnMouseReleased(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      val newTableDataOption = dragAndDrop.fieldsBeingDraggedInfo.get.flatMap(draggableFieldsInfo => {
        updateClosestDropTarget(event.getSceneX, event.getSceneY)
        dragAndDrop.closestDropTarget.get.map(dropTarget => {
          val tableDataWithFieldsRemoved = draggableFieldsInfo.draggableParent.removeFields(draggableFieldsInfo, tableData.get)
          dropTarget.fieldsDropped(draggableFieldsInfo, tableDataWithFieldsRemoved)
        })
      })
      dragAndDrop.fieldsBeingDraggedInfo.set(None)
      event.consume()
      newTableDataOption.foreach(newTableData => tableData.set(newTableData))
    }
  })
}

trait DropTarget extends Node {
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData):TableData
}

trait DropTargetContainer {
  val dragAndDrop:DragAndDrop
  dragAndDrop.register(this)
  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo):List[DropTarget]
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData):TableData
  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo)
  def removeDropTargets()

  dragAndDrop.fieldsBeingDraggedInfo.addListener(new ChangeListener[Option[DraggableFieldsInfo]] {
    def changed(observableValue:ObservableValue[_<:Option[DraggableFieldsInfo]],
                oldDraggableFieldsInfo:Option[DraggableFieldsInfo], newDraggableFieldsInfo:Option[DraggableFieldsInfo]) {
      newDraggableFieldsInfo match {
        case Some(draggableFieldsInfo) => addDropTargets(draggableFieldsInfo)
        case _ => removeDropTargets()
      }
    }
  })
}

trait DraggableParent {
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData):TableData
}

case class DraggableFieldsInfo(draggable:Draggable, draggableParent:DraggableParent)

trait DragAndDropNode extends Node {
  val tableDataProperty:SimpleObjectProperty[TableData]
  def setup()
  def description:ReadOnlyStringProperty

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {setup()}
  })
}
