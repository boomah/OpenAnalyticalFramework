package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.{Priority, VBox}
import javafx.geometry.Side
import com.openaf.table.lib.api._
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class MeasureAreaTreeNode(val measureAreaTree:MeasureAreaTree, tableDataProperty:SimpleObjectProperty[TableData],
                          dragAndDrop:DragAndDrop, dragAndDropContainer:DragAndDropContainer,
                          fieldBindings:ObservableMap[FieldID,StringBinding]) extends VBox {
  private val topNode = measureAreaTree.measureAreaTreeType match {
    case Left(field) => new FieldNode(field, dragAndDrop, dragAndDropContainer, tableDataProperty, fieldBindings)
    case Right(measureAreaLayout) => new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop,
      dragAndDropContainer, fieldBindings)
  }
  VBox.setVgrow(topNode, Priority.ALWAYS)
  getChildren.add(topNode)
  if (measureAreaTree.hasChildren) {
    val childMeasureLayoutNode = new MeasureAreaLayoutNode(measureAreaTree.childMeasureAreaLayout, tableDataProperty,
      dragAndDrop, dragAndDropContainer, fieldBindings)
    VBox.setVgrow(childMeasureLayoutNode, Priority.ALWAYS)
    getChildren.add(childMeasureLayoutNode)
  }

  def topFieldNodeOption = {
    topNode match {
      case fieldNode:FieldNode => Some(fieldNode)
      case _ => None
    }
  }

  def topMeasureAreaLayoutNodeOption = {
    topNode match {
      case measureAreaLayoutNode:MeasureAreaLayoutNode => Some(measureAreaLayoutNode)
      case _ => None
    }
  }

  def childMeasureAreaLayoutNodeOption = {
    val children = getChildren
    if (children.size == 2) Some(children.get(1).asInstanceOf[MeasureAreaLayoutNode]) else None
  }

  def topFieldNodes:Seq[FieldNode] = {
    topNode match {
      case fieldNode:FieldNode => List(fieldNode)
      case measureAreaLayoutNode:MeasureAreaLayoutNode => measureAreaLayoutNode.allFieldNodes
    }
  }

  def childFieldNodes:Seq[FieldNode] = {
    childMeasureAreaLayoutNodeOption.map(_.allFieldNodes).getOrElse(Nil)
  }

  def containsTopAndChildFieldNodes(draggableToFilterOut:Draggable) = {
    topFieldNodes.filterNot(_ == draggableToFilterOut).nonEmpty && childFieldNodes.filterNot(_ == draggableToFilterOut).nonEmpty
  }

  private def childMeasureAreaLayoutWithRemoval(draggableToRemove:Draggable) = {
    childMeasureAreaLayoutNodeOption.get.generateMeasureAreaLayoutWithRemoval(draggableToRemove)
  }

  def generateWithRemovalOption(draggableToRemove:Draggable):Option[MeasureAreaTree] = {
    val measureAreaTreeTypeOption = topNode match {
      case fieldNode:FieldNode if fieldNode != draggableToRemove => Some(Left(fieldNode.field))
      case measureAreaLayoutNode:MeasureAreaLayoutNode => Some(Right(measureAreaLayoutNode.generateMeasureAreaLayoutWithRemoval(draggableToRemove)))
      case _ => None
    }
    val numChildren = getChildren.size
    (measureAreaTreeTypeOption, numChildren) match {
      case (None, 1) => None
      case (None, 2) => {
        val newTopMeasureLayoutArea = childMeasureAreaLayoutWithRemoval(draggableToRemove)
        Some(MeasureAreaTree(Right(newTopMeasureLayoutArea)))
      }
      case (Some(measureAreaTreeType), 1) => Some(MeasureAreaTree(measureAreaTreeType))
      case (Some(measureAreaTreeType), 2) => Some(MeasureAreaTree(measureAreaTreeType, childMeasureAreaLayoutWithRemoval(draggableToRemove)))
      case unexpected => throw new IllegalStateException(s"A MeasureAreaTreeNode should only ever have 1 or 2 children $unexpected")
    }
  }

  def generateWithAdditionOption(nodeSide:NodeSide, draggableFieldsInfo:DraggableFieldsInfo):Option[MeasureAreaTree] = {
    // If we're adding a node that has been moved from this MeasureAreaTreeNode, exclude it from the generation.
    val measureAreaTreeTypeOption = topNode match {
      case fieldNode:FieldNode if fieldNode == nodeSide.node => {
        nodeSide.side match {
          case Side.LEFT => Some(Right(MeasureAreaLayout.fromFields(draggableFieldsInfo.draggable.fields ::: fieldNode.fields)))
          case Side.RIGHT => Some(Right(MeasureAreaLayout.fromFields(fieldNode.fields ::: draggableFieldsInfo.draggable.fields)))
          case Side.TOP => Some(Right(MeasureAreaLayout(draggableFieldsInfo.draggable.fields, fieldNode.fields)))
          case Side.BOTTOM => Some(Right(MeasureAreaLayout(fieldNode.field, draggableFieldsInfo.draggable.fields)))
        }
      }
      case fieldNode:FieldNode if fieldNode != draggableFieldsInfo.draggable => Some(Left(fieldNode.field))
      case measureAreaLayoutNode:MeasureAreaLayoutNode => Some(Right(measureAreaLayoutNode.generateMeasureAreaLayoutWithAddition(nodeSide, draggableFieldsInfo)))
      case _ => None
    }
    val newMeasureAreaTreeOption = childMeasureAreaLayoutNodeOption match {
      case Some(childMeasureAreaLayoutNode) => {
        val childMeasureAreaLayout = childMeasureAreaLayoutNode.generateMeasureAreaLayoutWithAddition(nodeSide, draggableFieldsInfo)
        measureAreaTreeTypeOption match {
          case Some(measureAreaTreeType) => Some(MeasureAreaTree(measureAreaTreeType, childMeasureAreaLayout))
          case _ => Some(MeasureAreaTree(Right(childMeasureAreaLayout)))
        }
      }
      case _ => measureAreaTreeTypeOption.map(measureAreaTreeType => MeasureAreaTree(measureAreaTreeType))
    }
    if (nodeSide.node == this) {
      val addedMeasureAreaTree =  MeasureAreaTree(draggableFieldsInfo.draggable.fields :_*)
      val newMeasureAreaLayout = nodeSide.side match {
        case Side.LEFT => MeasureAreaLayout(addedMeasureAreaTree :: newMeasureAreaTreeOption.toList ::: Nil)
        case Side.RIGHT => MeasureAreaLayout(newMeasureAreaTreeOption.toList ::: addedMeasureAreaTree :: Nil)
        case unexpected => throw new IllegalStateException(s"A MeasureAreaTreeNode should never have this side $unexpected")
      }
      Some(MeasureAreaTree(Right(newMeasureAreaLayout)))
    } else {
      newMeasureAreaTreeOption
    }
  }
}
