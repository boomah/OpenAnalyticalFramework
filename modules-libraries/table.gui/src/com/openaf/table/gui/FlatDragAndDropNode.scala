package com.openaf.table.gui

import javafx.scene.layout.{Pane, FlowPane, StackPane}
import com.openaf.table.api.{TableData, Field}
import javafx.scene.control.Label

trait FlatDragAndDropNode extends StackPane with DragAndDropNode with DropTarget with DropTargetContainer with DraggableParent {
  def fields(tableDataOption:Option[TableData]):List[Field]
  def withNewFields(fields:List[Field], tableData:TableData):TableData
  def fields:List[Field] = fields(None)

  private var dropTargetMap = Map.empty[DropTarget,Option[Draggable]]

  private val descriptionLabel = new Label
  descriptionLabel.textProperty.bind(description)

  private val mainContent = new FlowPane
  private val dropTargetPane = new Pane
  dropTargetPane.setMouseTransparent(true)
  getChildren.addAll(mainContent, dropTargetPane)

  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {
    if (fields.nonEmpty) {
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

  def setup() {
    val currentFields = fields
    mainContent.getChildren.clear()
    if (currentFields.isEmpty) {
      mainContent.getChildren.add(descriptionLabel)
    } else {
      val fieldNodes = currentFields.map(field => new FieldNode(field, dragAndDrop, this, tableDataProperty))
      mainContent.getChildren.addAll(fieldNodes.toArray :_*)
    }
  }

  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val currentFields = fields(Some(tableData))
    val newFields = dropTargetMap(dropTarget) match {
      case Some(draggable) => {
        def generateFields(children:Array[_]) = {
          val index = children.indexOf(draggable)
          currentFields.zipWithIndex.flatMap{case (field,i) => {
            if (index == i) {
              field :: draggableFieldsInfo.draggable.fields
            } else {
              List(field)
            }
          }}
        }
        val internalDrag = mainContent.getChildren.contains(draggableFieldsInfo.draggable)
        val childrenToUse = if (internalDrag) {
          mainContent.getChildren.toArray.filterNot(_ == draggableFieldsInfo.draggable)
        } else {
          mainContent.getChildren
        }.toArray
        generateFields(childrenToUse)
      }
      case _ => draggableFieldsInfo.draggable.fields ::: currentFields
    }
    withNewFields(newFields, tableData)
  }

  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val updatedFields = mainContent.getChildren.toArray.flatMap(child => if (child == draggableFieldsInfo.draggable) None else Some(child))
      .collect{case (draggable:Draggable) => draggable.fields}.flatten.toList
    withNewFields(updatedFields, tableData)
  }

  def removeDropTargets() {
    dropTargetPane.getChildren.clear()
    dropTargetMap = Map.empty
  }

  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    withNewFields(draggableFieldsInfo.draggable.fields, tableData)
  }

  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = if (fields.isEmpty) List(this) else dropTargetMap.keySet.toList
}