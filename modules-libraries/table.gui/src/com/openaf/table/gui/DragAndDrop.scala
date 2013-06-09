package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}
import collection.mutable
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.scene.layout.{Region, Pane, FlowPane, StackPane}
import javafx.scene.control.Label
import javafx.geometry.Side
import com.openaf.table.gui.binding.TableLocaleStringBinding
import java.util.Locale
import com.openaf.table.lib.api.{TableData, Field}

class DragAndDrop {
  val fieldsBeingDraggedInfo = new SimpleObjectProperty[Option[DraggableFieldsInfo]](None)
  val closestDropTarget = new SimpleObjectProperty[Option[DropTarget]](None)

  private val dropTargetContainers = new mutable.HashSet[DropTargetContainer]
  private var removeDropTargetOption:Option[DropTarget] = None

  def register(dropTargetContainer:DropTargetContainer) {dropTargetContainers += dropTargetContainer}
  def setRemoveDropTarget(removeDropTarget:DropTarget) {removeDropTargetOption = Some(removeDropTarget)}

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

  private def shouldRemove(mouseSceneX:Double, mouseSceneY:Double) = {
    val removeDistance = -30
    ((mouseSceneX < removeDistance) || (mouseSceneY < removeDistance))
  }

  def updateClosestDropTarget(mouseSceneX:Double, mouseSceneY:Double) {
    val dropTarget = (shouldRemove(mouseSceneX, mouseSceneY), removeDropTargetOption) match {
      case (true, Some(removeDropTarget)) => removeDropTarget
      case _ => closestDropTarget(mouseSceneX, mouseSceneY)
    }
    closestDropTarget.set(Some(dropTarget))
  }
}

trait Draggable extends Region {
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
      dragAndDrop.closestDropTarget.set(None)
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

trait DragAndDropNode extends StackPane with DropTargetContainer with DraggableParent {
  val tableDataProperty:SimpleObjectProperty[TableData]
  def descriptionID:String
  def locale:SimpleObjectProperty[Locale]
  def fields(tableDataOption:Option[TableData]):List[Field]
  protected def fields:List[Field] = fields(None)
  protected def hasFields = fields.nonEmpty
  def nodes:List[Node]
  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetMap.keySet.toList
  def removeDropTargets() {
    dropTargetPane.getChildren.clear()
    dropTargetMap = Map.empty
  }
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo):Map[DropTarget,NodeSide]
  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {
    dropTargetMap = dropTargetsToNodeSide(draggableFieldsInfo)
    dropTargetPane.getChildren.addAll(dropTargetMap.keySet.toArray :_*)
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {setup()}
  })

  protected var dropTargetMap = Map.empty[DropTarget,NodeSide]

  private val descriptionLabel = new Label
  descriptionLabel.textProperty.bind(new TableLocaleStringBinding(locale, descriptionID))

  protected val mainContent = new FlowPane
  protected val dropTargetPane = new Pane
  dropTargetPane.setMouseTransparent(true)
  getChildren.addAll(mainContent, dropTargetPane)

  def setup() {
    mainContent.getChildren.clear()
    val nodesToAdd = if (fields.isEmpty) Array(descriptionLabel) else nodes.toArray
    mainContent.getChildren.addAll(nodesToAdd :_*)
  }
}

case class NodeSide(node:Node, side:Side)