package com.openaf.table.gui

import javafx.geometry.Side
import scala.collection.JavaConversions._
import javafx.scene.control.Label
import com.openaf.table.lib.api.{FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

trait FlatDragAndDropContainerNode extends DragAndDropContainerNode {
  def fieldBindings:ObservableMap[FieldID,StringBinding]
  def withNewFields(fields:List[Field[_]], tableData:TableData):TableData
  def fields(tableDataOption:Option[TableData]):List[Field[_]]
  protected def fields:List[Field[_]] = fields(None)

  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = {
    val currentChildren = mainContent.getChildren
    if (fields.isEmpty) {
      val label = currentChildren.head.asInstanceOf[Label]
      val (dropTarget1, dropTarget2) = DropTargetNode.createDropTargetNodesForLabel(label, this)
      Map(dropTarget1 -> NodeSide(label, Side.LEFT), dropTarget2 -> NodeSide(label, Side.LEFT))
    } else {
      def nextDraggableOption(i:Int) = if (i < currentChildren.length) Some(currentChildren(i)) else None
      currentChildren.zipWithIndex.flatMap{case (currentDraggableNode,i) => {
        val currentDraggable = currentDraggableNode.asInstanceOf[Draggable]
        if (currentDraggable == draggableFieldsInfo.draggable) {
          Nil
        } else {
          val xDelta = if (i == (currentChildren.length - 1)) 1 else 2
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
  }

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

  private def fullSetup(fields:List[Field[_]]) {
    val nodes = fields.map(field => new FieldNode(field, dragAndDrop, this, tableDataProperty, fieldBindings))
    mainContent.getChildren.clear()
    mainContent.getChildren.addAll(nodes.toArray:_*)
  }

  def setup(oldTableDataOption:Option[TableData], newTableData:TableData) {
    val newFields = fields(Some(newTableData))
    if (newFields.isEmpty) {
      setupForEmpty()
    } else {
      oldTableDataOption match {
        case None => fullSetup(newFields)
        case oldTableDataSome@Some(oldTableData) => {
          val oldFields = fields(oldTableDataSome)
          if (oldFields != newFields) {
            fullSetup(newFields)
          }
        }
      }
    }
  }
}