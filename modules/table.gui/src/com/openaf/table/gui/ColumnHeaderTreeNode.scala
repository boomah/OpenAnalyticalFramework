package com.openaf.table.gui

import javafx.scene.layout.{Priority, VBox}
import javafx.geometry.Side
import com.openaf.table.lib.api._
import com.openaf.table.lib.api.ColumnHeaderLayout.ColumnHeaderTreeType
import collection.immutable.Seq

class ColumnHeaderTreeNode(val columnHeaderTree:ColumnHeaderTree, dragAndDropContainer:DragAndDropContainer,
                           tableFields:OpenAFTableFields) extends VBox {
  getStyleClass.add("column-header-tree-node")
  private val topNode = columnHeaderTree.columnHeaderTreeType match {
    case Left(field) => new FieldNode(field, dragAndDropContainer, tableFields)
    case Right(columnHeaderLayout) => new ColumnHeaderLayoutNode(columnHeaderLayout, dragAndDropContainer, tableFields)
  }
  VBox.setVgrow(topNode, Priority.ALWAYS)
  getChildren.add(topNode)
  if (columnHeaderTree.hasChildren) {
    val childColumnHeaderNode = new ColumnHeaderLayoutNode(columnHeaderTree.childColumnHeaderLayout,
      dragAndDropContainer, tableFields)
    VBox.setVgrow(childColumnHeaderNode, Priority.ALWAYS)
    getChildren.add(childColumnHeaderNode)
  }

  def topFieldNodeOption:Option[FieldNode[_]] = {
    topNode match {
      case fieldNode:FieldNode[_] => Some(fieldNode)
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

  def topFieldNodes:Seq[FieldNode[_]] = {
    topNode match {
      case fieldNode:FieldNode[_] => List(fieldNode)
      case columnHeaderLayoutNode:ColumnHeaderLayoutNode => columnHeaderLayoutNode.allFieldNodes
    }
  }

  def childFieldNodes:Seq[FieldNode[_]] = {
    childColumnHeaderLayoutNodeOption.map(_.allFieldNodes).getOrElse(Nil)
  }

  def containsTopAndChildFieldNodes(draggableToFilterOut:Draggable) = {
    topFieldNodes.filterNot(_ == draggableToFilterOut).nonEmpty && childFieldNodes.filterNot(_ == draggableToFilterOut).nonEmpty
  }

  def generateWithAdditionOption(nodeSide:NodeSide, draggableFieldsInfo:DraggableFieldsInfo):Option[ColumnHeaderTree] = {
    // If we're adding a node that has been moved from this ColumnHeaderTreeNode, exclude it from the generation.
    val columnHeaderTreeTypeOption:Option[ColumnHeaderTreeType] = topNode match {
      case fieldNode:FieldNode[_] if fieldNode == nodeSide.node => {
        nodeSide.side match {
          case Side.LEFT => Some(Right(ColumnHeaderLayout.fromFields(draggableFieldsInfo.fields ::: fieldNode.fields)))
          case Side.RIGHT => Some(Right(ColumnHeaderLayout.fromFields(fieldNode.fields ::: draggableFieldsInfo.fields)))
          case Side.TOP => Some(Right(ColumnHeaderLayout(draggableFieldsInfo.fields, fieldNode.fields)))
          case Side.BOTTOM => Some(Right(ColumnHeaderLayout(fieldNode.field, draggableFieldsInfo.fields)))
        }
      }
      case fieldNode:FieldNode[_] if fieldNode != draggableFieldsInfo.draggable => Some(Left(fieldNode.field))
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
      val addedColumnHeaderTree =  ColumnHeaderTree(draggableFieldsInfo.fields :_*)
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
