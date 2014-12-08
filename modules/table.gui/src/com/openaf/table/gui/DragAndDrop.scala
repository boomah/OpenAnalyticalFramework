package com.openaf.table.gui

import javafx.beans.property.{Property, SimpleObjectProperty}
import javafx.scene.{Cursor, Node}
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}
import collection.mutable
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.scene.layout.{Region, Pane, FlowPane, StackPane}
import javafx.scene.control.Label
import javafx.geometry.Side
import com.openaf.table.gui.binding.TableLocaleStringBinding
import java.util.Locale
import com.openaf.table.lib.api.{TableState, Field}
import javafx.scene.image.{Image, ImageView}

class DragAndDrop {
  val fieldsBeingDraggedInfo = new SimpleObjectProperty[Option[DraggableFieldsInfo]](None)
  val closestDropTarget = new SimpleObjectProperty[Option[DropTarget]](None)
  val dragPane = new Pane
  dragPane.setMouseTransparent(true)
  private val dragImageView = new ImageView
  dragImageView.setId("drag-image-view")
  var dragOffset = (0.0, 0.0)

  private val dragAndDropContainers = new mutable.HashSet[DragAndDropContainer]
  private var removeDropTargetOption:Option[DropTarget] = None

  def register(dragAndDropContainer:DragAndDropContainer) {dragAndDropContainers += dragAndDropContainer}
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
    val dropTargets = dragAndDropContainers.flatMap(_.dropTargets(draggableFieldsInfo))
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
    (mouseSceneX < removeDistance) || (mouseSceneY < removeDistance)
  }

  def updateClosestDropTarget(mouseSceneX:Double, mouseSceneY:Double) {
    val dropTarget = (shouldRemove(mouseSceneX, mouseSceneY), removeDropTargetOption) match {
      case (true, Some(removeDropTarget)) => removeDropTarget
      case _ => closestDropTarget(mouseSceneX, mouseSceneY)
    }
    closestDropTarget.set(Some(dropTarget))
  }

  private def dragPanelMousePoints(event:MouseEvent) = {
    val dragPaneMousePoint = dragPane.sceneToLocal(event.getSceneX, event.getSceneY)
    (dragPaneMousePoint.getX, dragPaneMousePoint.getY)
  }
  def addDragImage(image:Image, event:MouseEvent) {
    dragImageView.setImage(image)
    dragImageView.setTranslateX(-dragOffset._1)
    dragImageView.setTranslateY(-dragOffset._2)
    val (dragPaneX, dragPaneY) = dragPanelMousePoints(event)
    dragImageView.relocate(dragPaneX, dragPaneY)
    dragPane.getChildren.add(dragImageView)
  }
  def updateDragImagePosition(event:MouseEvent) {
    val (dragPaneX, dragPaneY) = dragPanelMousePoints(event)
    dragImageView.relocate(dragPaneX, dragPaneY)
  }
  def clearDragPane() {dragPane.getChildren.clear()}
}

trait Draggable extends Region {
  def dragAndDrop:DragAndDrop
  def dragAndDropContainer:DragAndDropContainer
  def fields:List[Field[_]]
  // When dropped here, nothing will happen. Usually just the Draggable itself, but in the case of a Draggable being
  // dragged from the AllFieldsArea, the AllFieldsArea scene bounds are used.
  def noOpSceneBounds = localToScene(getBoundsInLocal)
  def requestTableStateProperty:Property[TableState]
  def dragImage:Image
  def isParent = false

  private def updateClosestDropTarget(event:MouseEvent) {
    val (mouseSceneX, mouseSceneY) = (event.getSceneX, event.getSceneY)
    if (noOpSceneBounds.contains(mouseSceneX, mouseSceneY)) {
      dragAndDrop.closestDropTarget.set(None)
    } else {
      dragAndDrop.updateClosestDropTarget(mouseSceneX, mouseSceneY)
    }
  }

  addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY) {dragAndDrop.dragOffset = (event.getX, event.getY)}
    }
  })

  addEventHandler(MouseEvent.DRAG_DETECTED, new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY) {
        dragAndDrop.fieldsBeingDraggedInfo.set(Some(DraggableFieldsInfo(Draggable.this, dragAndDropContainer)))
        getScene.setCursor(Cursor.CLOSED_HAND)
        dragAndDrop.addDragImage(dragImage, event)
        updateClosestDropTarget(event)
      }
      event.consume()
    }
  })

  addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      dragAndDrop.fieldsBeingDraggedInfo.get.foreach(_ => {
        dragAndDrop.updateDragImagePosition(event)
        updateClosestDropTarget(event)
      })      
      event.consume()
    }
  })

  addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      event.consume()
      dragAndDrop.fieldsBeingDraggedInfo.get.foreach(draggableFieldsInfo => {
        updateClosestDropTarget(event)
        val newTableStateOption = dragAndDrop.closestDropTarget.get.map(dropTarget => {
          val tableStateWithFieldsRemoved = requestTableStateProperty.getValue.remove(draggableFieldsInfo.fields)
          dropTarget.fieldsDropped(draggableFieldsInfo, tableStateWithFieldsRemoved)
        })
        getScene.setCursor(Cursor.DEFAULT)
        dragAndDrop.clearDragPane()
        dragAndDrop.closestDropTarget.set(None)
        dragAndDrop.fieldsBeingDraggedInfo.set(None)
        newTableStateOption.foreach(newTableState => requestTableStateProperty.setValue(newTableState.generateFieldKeys))
      })
    }
  })

  addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY && event.getClickCount == 2 && fields.nonEmpty && !isParent) {moveField()}
    }
  })

  private def moveField() {
    val currentTableState = requestTableStateProperty.getValue
    val tableStateWithRemoved = currentTableState.remove(fields)
    val tableStateToUse = if (tableStateWithRemoved == currentTableState) {
      val field = fields.head
      if (field.fieldType.isDimension) {
        val newRowHeaderFields = currentTableState.tableLayout.rowHeaderFields :+ field
        currentTableState.withRowHeaderFields(newRowHeaderFields)
      } else {
        val newColumnHeaderLayout = currentTableState.tableLayout.columnHeaderLayout.addFieldToRight(field)
        currentTableState.withColumnHeaderLayout(newColumnHeaderLayout)
      }
    } else {
      tableStateWithRemoved
    }
    requestTableStateProperty.setValue(tableStateToUse.generateFieldKeys)
  }
}

trait DropTarget extends Node {
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableState:TableState):TableState
}

trait DragAndDropContainer {
  def dragAndDrop:DragAndDrop
  dragAndDrop.register(this)
  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo):List[DropTarget]
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableState:TableState):TableState
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

case class DraggableFieldsInfo(draggable:Draggable, dragAndDropContainer:DragAndDropContainer) {
  def fields = draggable.fields
}

trait DragAndDropContainerNode extends StackPane with DragAndDropContainer {
  getStyleClass.add("drag-and-drop-container-node")
  def requestTableStateProperty:Property[TableState]
  def descriptionID:String
  def locale:Property[Locale]
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
  def setup(oldTableStateOption:Option[TableState], newTableState:TableState)

  requestTableStateProperty.addListener(new ChangeListener[TableState] {
    def changed(observable:ObservableValue[_<:TableState], oldTableState:TableState, newTableState:TableState) {
      setup(Option(oldTableState), newTableState)
    }
  })

  protected var dropTargetMap = Map.empty[DropTarget,NodeSide]

  private val descriptionLabel = new Label
  descriptionLabel.getStyleClass.add("description-label")
  descriptionLabel.textProperty.bind(new TableLocaleStringBinding(descriptionID, locale))

  protected val mainContent = new FlowPane
  mainContent.getStyleClass.add("main-content")
  protected val dropTargetPane = new Pane
  dropTargetPane.setMouseTransparent(true)
  getChildren.addAll(mainContent, dropTargetPane)

  protected def setupForEmpty() {
    mainContent.getChildren.clear()
    mainContent.getChildren.add(descriptionLabel)
  }
}

case class NodeSide(node:Node, side:Side)