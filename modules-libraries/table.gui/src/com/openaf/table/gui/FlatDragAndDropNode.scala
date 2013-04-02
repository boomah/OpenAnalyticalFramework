package com.openaf.table.gui

import com.openaf.table.api.{TableData, Field}
import javafx.geometry.Side

trait FlatDragAndDropNode extends DragAndDropNode {
  def withNewFields(fields:List[Field], tableData:TableData):TableData

  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = {
    val currentDraggables = mainContent.getChildren.toArray
    def nextDraggableOption(i:Int) = if (i < currentDraggables.length) Some(currentDraggables(i)) else None
    currentDraggables.zipWithIndex.flatMap{case (currentDraggableAny,i) => {
      val currentDraggable = currentDraggableAny.asInstanceOf[Draggable]
      if (currentDraggable == draggableFieldsInfo.draggable) {
        Nil
      } else {
        val xDelta = if (i == (currentDraggables.length - 1)) 1 else 2
        if (i == 0) {
          val leftDropTarget = new DropTargetNode(this)
          leftDropTarget.layoutYProperty.bind(currentDraggable.heightProperty.divide(2).subtract(leftDropTarget.heightProperty.divide(2)))
          leftDropTarget.layoutXProperty.bind(currentDraggable.layoutXProperty)
          val leftDropTargetEntry = leftDropTarget -> NodeSide(currentDraggable, Side.LEFT)
          nextDraggableOption(1) match {
            case Some(nextChild) if nextChild == draggableFieldsInfo.draggable => List(leftDropTargetEntry)
            case _ => {
              val rightDropTarget = new DropTargetNode(this)
              rightDropTarget.layoutYProperty.bind(currentDraggable.heightProperty.divide(2).subtract(rightDropTarget.heightProperty.divide(2)))
              rightDropTarget.layoutXProperty.bind(currentDraggable.layoutXProperty
                .add(currentDraggable.layoutBoundsProperty.get.getWidth).subtract(rightDropTarget.widthProperty.divide(xDelta)))
              List(leftDropTargetEntry, rightDropTarget -> NodeSide(currentDraggable, Side.RIGHT))
            }
          }
        } else {
          nextDraggableOption(i + 1) match {
            case Some(nextChild) if nextChild == draggableFieldsInfo.draggable => Nil
            case _ => {
              val rightDropTarget = new DropTargetNode(this)
              rightDropTarget.layoutYProperty.bind(currentDraggable.heightProperty.divide(2).subtract(rightDropTarget.heightProperty.divide(2)))
              rightDropTarget.layoutXProperty.bind(currentDraggable.layoutXProperty
                .add(currentDraggable.layoutBoundsProperty.get.getWidth).subtract(rightDropTarget.widthProperty.divide(xDelta)))
              List(rightDropTarget -> NodeSide(currentDraggable, Side.RIGHT))
            }
          }
        }
      }
    }}.toMap
  }

  def nodes = fields.map(field => new FieldNode(field, dragAndDrop, this, tableDataProperty))

  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val currentFields = fields(Some(tableData))
    val nodeSide = dropTargetMap(dropTarget)
    val newFields = if (nodeSide.side == Side.RIGHT) {
      def generateFields(children:Array[_]) = {
        val index = children.indexOf(nodeSide.node)
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
    } else {
      draggableFieldsInfo.draggable.fields ::: currentFields
    }
    withNewFields(newFields, tableData)
  }

  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val updatedFields = mainContent.getChildren.toArray.flatMap(child => if (child == draggableFieldsInfo.draggable) None else Some(child))
      .collect{case (draggable:Draggable) => draggable.fields}.flatten.toList
    withNewFields(updatedFields, tableData)
  }

  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    withNewFields(draggableFieldsInfo.draggable.fields, tableData)
  }
}