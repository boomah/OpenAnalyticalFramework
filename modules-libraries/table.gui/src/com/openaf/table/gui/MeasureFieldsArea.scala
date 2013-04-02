package com.openaf.table.gui

import javafx.scene.layout._
import com.openaf.table.api.{MeasureAreaTree, MeasureAreaLayout, TableData}
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}
import javafx.geometry.Side

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends DragAndDropNode {
  getStyleClass.add("measure-fields-area")
  private val dropTargetsHelper = new MeasureFieldsAreaDropTargetsHelper(mainContent, dropTargetPane, this)

  def description = new SimpleStringProperty("Drop Measure and Column Fields Here")
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData.withMeasureAreaLayout(MeasureAreaLayout.fromFields(draggableFieldsInfo.draggable.fields))
  }
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetsHelper.dropTargetsToNodeSide(draggableFieldsInfo)
  def fields(tableDataOption:Option[TableData]) = measureAreaLayout(tableDataOption).allFields.toList
  def nodes = List(new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, this))
  private def measureAreaLayout(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.measureAreaLayout
  }
  private def measureAreaLayout:MeasureAreaLayout = measureAreaLayout(None)
  private def parentMeasureAreaLayoutNode = mainContent.getChildren.get(0).asInstanceOf[MeasureAreaLayoutNode]
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val newMeasureAreaLayout = parentMeasureAreaLayoutNode.generateMeasureAreaLayoutWithRemoval(draggableToRemove = draggableFieldsInfo.draggable)
    val normalisedNewMeasureAreaLayout = newMeasureAreaLayout.normalise
    println("RR " + normalisedNewMeasureAreaLayout)
    tableData.withMeasureAreaLayout(normalisedNewMeasureAreaLayout)
  }
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val nodeSide = dropTargetMap(dropTarget)
    val newMeasureAreaLayout = parentMeasureAreaLayoutNode.generateWithAddition(nodeSide, draggableFieldsInfo)
    tableData.withMeasureAreaLayout(newMeasureAreaLayout)
  }
}

class MeasureAreaLayoutNode(measureAreaLayout:MeasureAreaLayout, tableDataProperty:SimpleObjectProperty[TableData],
                            dragAndDrop:DragAndDrop, draggableParent:DraggableParent) extends HBox {
  setStyle("-fx-border-color: blue;")
  val measureAreaTreeNodes = measureAreaLayout.measureAreaTrees.map(measureAreaTree => {
    val measureAreaTreeNode = new MeasureAreaTreeNode(measureAreaTree, tableDataProperty, dragAndDrop, draggableParent)
    HBox.setHgrow(measureAreaTreeNode, Priority.ALWAYS)
    measureAreaTreeNode
  })
  getChildren.addAll(measureAreaTreeNodes.toArray :_*)

  def childMeasureAreaTreeNodes = getChildren.toArray.collect{case (measureAreaTreeNode:MeasureAreaTreeNode) => measureAreaTreeNode}

  def generateMeasureAreaLayoutWithRemoval(draggableToRemove:Draggable) = {
    val measureAreaTrees = measureAreaTreeNodes.flatMap(_.generateWithRemovalOption(draggableToRemove))
    MeasureAreaLayout(measureAreaTrees)
  }

  def generateWithAddition(nodeSide:NodeSide, draggableFieldsInfo:DraggableFieldsInfo) = {
    val measureAreaTrees = measureAreaTreeNodes.map(_.generateWithAddition(nodeSide, draggableFieldsInfo))
    if (nodeSide.node == this) {
      nodeSide.side match {
        case Side.TOP => {
          val measureAreaTreeType = draggableFieldsInfo.draggable.fields match {
            case field :: Nil => Left(field)
            case manyFields => Right(MeasureAreaLayout.fromFields(manyFields))
          }
          val childMeasureAreaLayout = MeasureAreaLayout(measureAreaTrees)
          val newMeasureAreaTree = MeasureAreaTree(measureAreaTreeType, childMeasureAreaLayout)
          MeasureAreaLayout(newMeasureAreaTree)
        }
        case Side.BOTTOM => {
          MeasureAreaLayout(measureAreaTrees)
        }
        case unexpected => throw new IllegalStateException(s"A MeasureAreaLayoutNode should never have this side $unexpected")
      }
    } else {
      MeasureAreaLayout(measureAreaTrees)
    }
  }
}

class MeasureAreaTreeNode(measureAreaTree:MeasureAreaTree, tableDataProperty:SimpleObjectProperty[TableData],
                          dragAndDrop:DragAndDrop, draggableParent:DraggableParent) extends VBox {
  setStyle("-fx-border-color: green;")
  private val topNode = measureAreaTree.measureAreaTreeType match {
    case Left(field) => new FieldNode(field, dragAndDrop, draggableParent, tableDataProperty)
    case Right(measureAreaLayout) => new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, draggableParent)
  }
  getChildren.add(topNode)
  if (measureAreaTree.hasChildren) {
    val childMeasureLayoutNode = new MeasureAreaLayoutNode(measureAreaTree.childMeasureAreaLayout, tableDataProperty, dragAndDrop, draggableParent)
    getChildren.add(childMeasureLayoutNode)
  }

  def fieldNodeOption = {
    topNode match {
      case fieldNode:FieldNode => Some(fieldNode)
      case _ => None
    }
  }

  def measureAreaLayoutOption = {
    topNode match {
      case measureAreaLayoutNode:MeasureAreaLayoutNode => Some(measureAreaLayoutNode)
      case _ => None
    }
  }

  private def bottomMeasureAreaLayoutWithRemoval(draggableToRemove:Draggable) = {
    getChildren.get(1).asInstanceOf[MeasureAreaLayoutNode].generateMeasureAreaLayoutWithRemoval(draggableToRemove)
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
        val newTopMeasureLayoutArea = bottomMeasureAreaLayoutWithRemoval(draggableToRemove)
        Some(MeasureAreaTree(Right(newTopMeasureLayoutArea)))
      }
      case (Some(measureAreaTreeType), 1) => Some(MeasureAreaTree(measureAreaTreeType))
      case (Some(measureAreaTreeType), 2) => Some(MeasureAreaTree(measureAreaTreeType, bottomMeasureAreaLayoutWithRemoval(draggableToRemove)))
      case unexpected => throw new IllegalStateException(s"A MeasureAreaTreeNode should only ever have 1 or 2 children $unexpected")
    }
  }

  def generateWithAddition(nodeSide:NodeSide, draggableFieldsInfo:DraggableFieldsInfo):MeasureAreaTree = {
    val measureAreaTreeType = topNode match {
      case fieldNode:FieldNode => Left(fieldNode.field)
      case measureAreaLayoutNode:MeasureAreaLayoutNode => Right(measureAreaLayoutNode.generateWithAddition(nodeSide, draggableFieldsInfo))
    }
    if (getChildren.size == 2) {
      val childMeasureAreaLayout = getChildren.get(1).asInstanceOf[MeasureAreaLayoutNode].generateWithAddition(nodeSide, draggableFieldsInfo)
      MeasureAreaTree(measureAreaTreeType, childMeasureAreaLayout)
    } else {
      MeasureAreaTree(measureAreaTreeType)
    }
  }
}