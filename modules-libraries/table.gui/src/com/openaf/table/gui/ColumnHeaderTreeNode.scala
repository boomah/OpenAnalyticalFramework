package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.{Priority, VBox}
import javafx.geometry.Side
import com.openaf.table.lib.api._
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class ColumnHeaderTreeNode(val columnHeaderTree:ColumnHeaderTree, tableDataProperty:SimpleObjectProperty[TableData],
                           dragAndDrop:DragAndDrop, dragAndDropContainer:DragAndDropContainer,
                           fieldBindings:ObservableMap[FieldID,StringBinding]) extends VBox {
  private val topNode = columnHeaderTree.columnHeaderTreeType match {
    case Left(field) => new FieldNode(field, dragAndDrop, dragAndDropContainer, tableDataProperty, fieldBindings)
    case Right(columnHeaderLayout) => new ColumnHeaderLayoutNode(columnHeaderLayout, tableDataProperty, dragAndDrop,
      dragAndDropContainer, fieldBindings)
  }
  VBox.setVgrow(topNode, Priority.ALWAYS)
  getChildren.add(topNode)
  if (columnHeaderTree.hasChildren) {
    val childColumnHeaderNode = new ColumnHeaderLayoutNode(columnHeaderTree.childColumnHeaderLayout, tableDataProperty,
      dragAndDrop, dragAndDropContainer, fieldBindings)
    VBox.setVgrow(childColumnHeaderNode, Priority.ALWAYS)
    getChildren.add(childColumnHeaderNode)
  }

  def topFieldNodeOption = {
    topNode match {
      case fieldNode:FieldNode => Some(fieldNode)
      case _ => None
    }
  }

  def topColumnHeaderLayoutNodeOption = {
    topNode match {
      case columnHeaderLayoutNode:ColumnHeaderLayoutNode => Some(columnHeaderLayoutNode)
      case _ => None
    }
  }

  def childColumnHeaderLayoutNodeOption = {
    val children = getChildren
    if (children.size == 2) Some(children.get(1).asInstanceOf[ColumnHeaderLayoutNode]) else None
  }

  def topFieldNodes:Seq[FieldNode] = {
    topNode match {
      case fieldNode:FieldNode => List(fieldNode)
      case columnHeaderLayoutNode:ColumnHeaderLayoutNode => columnHeaderLayoutNode.allFieldNodes
    }
  }

  def childFieldNodes:Seq[FieldNode] = {
    childColumnHeaderLayoutNodeOption.map(_.allFieldNodes).getOrElse(Nil)
  }

  def containsTopAndChildFieldNodes(draggableToFilterOut:Draggable) = {
    topFieldNodes.filterNot(_ == draggableToFilterOut).nonEmpty && childFieldNodes.filterNot(_ == draggableToFilterOut).nonEmpty
  }

  private def childColumnHeaderLayoutWithRemoval(draggableToRemove:Draggable) = {
    childColumnHeaderLayoutNodeOption.get.generateColumnHeaderLayoutWithRemoval(draggableToRemove)
  }

  def generateWithRemovalOption(draggableToRemove:Draggable):Option[ColumnHeaderTree] = {
    val columnHeaderTreeTypeOption = topNode match {
      case fieldNode:FieldNode if fieldNode != draggableToRemove => Some(Left(fieldNode.field))
      case columnHeaderLayoutNode:ColumnHeaderLayoutNode => Some(Right(columnHeaderLayoutNode.generateColumnHeaderLayoutWithRemoval(draggableToRemove)))
      case _ => None
    }
    val numChildren = getChildren.size
    (columnHeaderTreeTypeOption, numChildren) match {
      case (None, 1) => None
      case (None, 2) => {
        val newColumnHeaderLayoutArea = childColumnHeaderLayoutWithRemoval(draggableToRemove)
        Some(ColumnHeaderTree(Right(newColumnHeaderLayoutArea)))
      }
      case (Some(columnHeaderTreeType), 1) => Some(ColumnHeaderTree(columnHeaderTreeType))
      case (Some(columnHeaderTreeType), 2) => Some(ColumnHeaderTree(columnHeaderTreeType, childColumnHeaderLayoutWithRemoval(draggableToRemove)))
      case unexpected => throw new IllegalStateException(s"A ColumnHeaderTreeNode should only ever have 1 or 2 children $unexpected")
    }
  }

  def generateWithAdditionOption(nodeSide:NodeSide, draggableFieldsInfo:DraggableFieldsInfo):Option[ColumnHeaderTree] = {
    // If we're adding a node that has been moved from this ColumnHeaderTreeNode, exclude it from the generation.
    val columnHeaderTreeTypeOption = topNode match {
      case fieldNode:FieldNode if fieldNode == nodeSide.node => {
        nodeSide.side match {
          case Side.LEFT => Some(Right(ColumnHeaderLayout.fromFields(draggableFieldsInfo.draggable.fields ::: fieldNode.fields)))
          case Side.RIGHT => Some(Right(ColumnHeaderLayout.fromFields(fieldNode.fields ::: draggableFieldsInfo.draggable.fields)))
          case Side.TOP => Some(Right(ColumnHeaderLayout(draggableFieldsInfo.draggable.fields, fieldNode.fields)))
          case Side.BOTTOM => Some(Right(ColumnHeaderLayout(fieldNode.field, draggableFieldsInfo.draggable.fields)))
        }
      }
      case fieldNode:FieldNode if fieldNode != draggableFieldsInfo.draggable => Some(Left(fieldNode.field))
      case columnHeaderLayoutNode:ColumnHeaderLayoutNode => Some(Right(columnHeaderLayoutNode.generateColumnHeaderLayoutWithAddition(nodeSide, draggableFieldsInfo)))
      case _ => None
    }
    val newColumnHeaderTreeOption = childColumnHeaderLayoutNodeOption match {
      case Some(childColumnHeaderLayoutNode) => {
        val childColumnHeaderLayout = childColumnHeaderLayoutNode.generateColumnHeaderLayoutWithAddition(nodeSide, draggableFieldsInfo)
        columnHeaderTreeTypeOption match {
          case Some(columnHeaderTreeType) => Some(ColumnHeaderTree(columnHeaderTreeType, childColumnHeaderLayout))
          case _ => Some(ColumnHeaderTree(Right(childColumnHeaderLayout)))
        }
      }
      case _ => columnHeaderTreeTypeOption.map(columnHeaderTreeType => ColumnHeaderTree(columnHeaderTreeType))
    }
    if (nodeSide.node == this) {
      val addedColumnHeaderTree =  ColumnHeaderTree(draggableFieldsInfo.draggable.fields :_*)
      val newColumnHeaderLayout = nodeSide.side match {
        case Side.LEFT => ColumnHeaderLayout(addedColumnHeaderTree :: newColumnHeaderTreeOption.toList ::: Nil)
        case Side.RIGHT => ColumnHeaderLayout(newColumnHeaderTreeOption.toList ::: addedColumnHeaderTree :: Nil)
        case unexpected => throw new IllegalStateException(s"A ColumnHeaderTreeNode should never have this side $unexpected")
      }
      Some(ColumnHeaderTree(Right(newColumnHeaderLayout)))
    } else {
      newColumnHeaderTreeOption
    }
  }
}
