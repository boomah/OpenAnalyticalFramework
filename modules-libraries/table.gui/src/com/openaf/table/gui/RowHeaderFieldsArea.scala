package com.openaf.table.gui

import javafx.scene.layout.{Pane, StackPane, FlowPane}
import javafx.scene.control.Label
import com.openaf.table.api.TableData
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ObservableValue, ChangeListener}

class RowHeaderFieldsArea(tableDataProperty:SimpleObjectProperty[TableData], dragAndDrop:DragAndDrop)
  extends StackPane with DropTargetContainer with DropTarget with DraggableParent {

  private var dropTargetMap = Map.empty[DropTarget,Option[Draggable]]
  private val descriptionLabel = new Label("Drop Row Header Fields Here")

  private val mainContent = new FlowPane
  private val dropTargetPane = new Pane
  dropTargetPane.setMouseTransparent(true)
  getChildren.addAll(mainContent, dropTargetPane)

  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = {
    if (rowHeaderFields.isEmpty) List(this) else dropTargetMap.keySet.toList
  }
  dragAndDrop.register(this)

  private def rowHeaderFields = tableDataProperty.get.tableState.tableLayout.rowHeaderFields

  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData.withTableState(tableData.tableState.withRowHeaderFields(draggableFieldsInfo.draggable.fields))
  }

  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val currentRowHeaderFields = tableData.tableState.tableLayout.rowHeaderFields
    val newRowHeaderFields = dropTargetMap(dropTarget) match {
      case Some(draggable) => {
        def generateFields(children:Array[_]) = {
          val index = children.indexOf(draggable)
          currentRowHeaderFields.zipWithIndex.flatMap{case (field,i) => {
            if (index == i) {
              field :: draggableFieldsInfo.draggable.fields
            } else {
              List(field)
            }
          }}
        }
        val rowHeaderToRowHeaderDrag = mainContent.getChildren.contains(draggableFieldsInfo.draggable)
        val childrenToUse = if (rowHeaderToRowHeaderDrag) {
          mainContent.getChildren.toArray.filterNot(_ == draggableFieldsInfo.draggable)
        } else {
          mainContent.getChildren
        }.toArray
        generateFields(childrenToUse)
      }
      case _ => draggableFieldsInfo.draggable.fields ::: currentRowHeaderFields
    }
    tableData.withTableState(tableData.tableState.withRowHeaderFields(newRowHeaderFields))
  }

  dragAndDrop.fieldsBeingDraggedInfo.addListener(new ChangeListener[Option[DraggableFieldsInfo]] {
    def changed(observableValue:ObservableValue[_<:Option[DraggableFieldsInfo]],
                oldDraggableFieldsInfo:Option[DraggableFieldsInfo], newDraggableFieldsInfo:Option[DraggableFieldsInfo]) {
      newDraggableFieldsInfo match {
        case Some(draggableFieldsInfo) => addDropTargets(draggableFieldsInfo)
        case _ => removeDropTargets()
      }
    }
  })

  private def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {
    if (rowHeaderFields.nonEmpty) {
      val currentDraggables = mainContent.getChildren.toArray
      def nextDraggableOption(i:Int) = if (i < currentDraggables.length) Some(currentDraggables(i)) else None
      dropTargetMap = currentDraggables.zipWithIndex.flatMap{case (currentDraggableAny,i) => {
        val currentDraggable = currentDraggableAny.asInstanceOf[Draggable]
        val draggablesMatch = (currentDraggable == draggableFieldsInfo.draggable)
        if (!draggablesMatch && (i == 0)) {
          val leftDropTarget = new DropTargetNode(this)
          leftDropTarget.setPrefHeight(currentDraggable.getLayoutBounds.getHeight)
          leftDropTarget.layoutXProperty.bind(currentDraggable.layoutXProperty.subtract(leftDropTarget.widthProperty.divide(2)))
          val leftDropTargetEntry = leftDropTarget -> None
          nextDraggableOption(1) match {
            case Some(nextChild) if nextChild == draggableFieldsInfo.draggable => List(leftDropTargetEntry)
            case _ => {
              val rightDropTarget = new DropTargetNode(this)
              rightDropTarget.setPrefHeight(currentDraggable.getLayoutBounds.getHeight)
              rightDropTarget.layoutXProperty.bind(currentDraggable.layoutXProperty
                .add(currentDraggable.layoutBoundsProperty.get.getWidth).subtract(rightDropTarget.widthProperty.divide(2)))
              List(leftDropTargetEntry, rightDropTarget -> Some(currentDraggable))
            }
          }
        } else if (!draggablesMatch) {
          nextDraggableOption(i + 1) match {
            case Some(nextChild) if nextChild == draggableFieldsInfo.draggable => Nil
            case _ => {
              val rightDropTarget = new DropTargetNode(this)
              rightDropTarget.setPrefHeight(currentDraggable.getLayoutBounds.getHeight)
              rightDropTarget.layoutXProperty.bind(currentDraggable.layoutXProperty
                .add(currentDraggable.layoutBoundsProperty.get.getWidth).subtract(rightDropTarget.widthProperty.divide(2)))
              List(rightDropTarget -> Some(currentDraggable))
            }
          }
        } else {
          Nil
        }
      }}.toMap
      dropTargetPane.getChildren.addAll(dropTargetMap.keySet.toArray :_*)
    }
  }

  private def removeDropTargets() {
    dropTargetPane.getChildren.clear()
    dropTargetMap = Map.empty
  }

  private def setup() {
    val rowHeaderFields = tableDataProperty.get.tableState.tableLayout.rowHeaderFields
    mainContent.getChildren.clear()
    if (rowHeaderFields.isEmpty) {
      mainContent.getChildren.add(descriptionLabel)
    } else {
      val fieldNodes = rowHeaderFields.map(field => new FieldNode(field, dragAndDrop, this, tableDataProperty))
      mainContent.getChildren.addAll(fieldNodes.toArray :_*)
    }
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {setup()}
  })

  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val updatedFields = mainContent.getChildren.toArray.flatMap(child => if (child == draggableFieldsInfo.draggable) None else Some(child))
      .collect{case (draggable:Draggable) => draggable.fields}.flatten.toList
    tableData.withTableState(tableData.tableState.withRowHeaderFields(updatedFields))
  }
}
