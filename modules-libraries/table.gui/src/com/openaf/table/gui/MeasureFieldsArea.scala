package com.openaf.table.gui

import javafx.scene.layout._
import com.openaf.table.api.{MeasureAreaTree, MeasureAreaLayout, TableData}
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}
import javafx.scene.Node

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends DragAndDropNode {
  getStyleClass.add("measure-fields-area")

  def description = new SimpleStringProperty("Drop Measure and Column Fields Here")
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = tableData
  def dropTargetsToDraggable(draggableFieldsInfo:DraggableFieldsInfo) = {
    val currentMeasureAreaLayoutNode = mainContent.getChildren.get(0).asInstanceOf[MeasureAreaLayoutNode]
    allNodes(currentMeasureAreaLayoutNode).filterNot(_ == draggableFieldsInfo.draggable).flatMap(node => {
      node match {
        case fieldNode:FieldNode => dropTargetsForFieldNode(fieldNode)
        case measureAreaTreeNode:MeasureAreaTreeNode => dropTargetsForMeasureAreaTreeNode(measureAreaTreeNode)
        case measureAreaLayoutNode:MeasureAreaLayoutNode => dropTargetsForMeasureAreaLayoutNode(measureAreaLayoutNode)
        case _ => Nil
      }
    }).toMap
  }
  private def allNodes(pane:Pane):List[Node] = {
    val paneChildNodes = pane.getChildren.toArray.toList
    val deepChildNodes = paneChildNodes.flatMap(nodeAny => {
      nodeAny match {
        case childPane:Pane => allNodes(childPane)
        case node:Node => List(node)
      }
    })
    pane :: deepChildNodes
  }
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData
  }
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = tableData
  def fields(tableDataOption:Option[TableData]) = measureAreaLayout(tableDataOption).allFields.toList
  def nodes = List(new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, this))
  private def measureAreaLayout(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.measureAreaLayout
  }
  private def measureAreaLayout:MeasureAreaLayout = measureAreaLayout(None)
  private def nodeWidthAndHeight(node:Node) = (node.getLayoutBounds.getWidth, node.getLayoutBounds.getHeight)
  private def dropTargetsForFieldNode(fieldNode:FieldNode) = {
    val (fieldNodeWidth, fieldNodeHeight) = nodeWidthAndHeight(fieldNode)
    val fieldNodeSceneBounds = fieldNode.localToScene(fieldNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(fieldNodeSceneBounds)

    val topDropTargetNode = new DropTargetNode(this)
    topDropTargetNode.setPrefWidth(fieldNodeWidth / 2)
    topDropTargetNode.setPrefHeight(fieldNodeHeight / 4)
    topDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + (fieldNodeWidth / 4))
    topDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + fieldNodeHeight / 4)

    val bottomDropTargetNode = new DropTargetNode(this)
    bottomDropTargetNode.setPrefWidth(fieldNodeWidth / 2)
    bottomDropTargetNode.setPrefHeight(fieldNodeHeight / 4)
    bottomDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + (fieldNodeWidth / 4))
    bottomDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + fieldNodeHeight / 2)

    val leftDropTargetNode = new DropTargetNode(this)
    leftDropTargetNode.setPrefWidth(fieldNodeWidth / 8)
    leftDropTargetNode.setPrefHeight(fieldNodeHeight / 2)
    leftDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + (fieldNodeWidth / 16))
    leftDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + fieldNodeHeight / 4)

    val rightDropTargetNode = new DropTargetNode(this)
    rightDropTargetNode.setPrefWidth(fieldNodeWidth / 8)
    rightDropTargetNode.setPrefHeight(fieldNodeHeight / 2)
    rightDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + (fieldNodeWidth / 16) * 13)
    rightDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + fieldNodeHeight / 4)

    val someFieldNode = Some(fieldNode)
    List(topDropTargetNode, bottomDropTargetNode, leftDropTargetNode, rightDropTargetNode).map(_ -> someFieldNode)
  }
  private def dropTargetsForMeasureAreaTreeNode(measureAreaTreeNode:MeasureAreaTreeNode) = {
    val (_, nodeHeight) = nodeWidthAndHeight(measureAreaTreeNode)
    val nodeSceneBounds = measureAreaTreeNode.localToScene(measureAreaTreeNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(nodeSceneBounds)

    val shouldCreateDropTargetNodes = measureAreaTreeNode.getChildren.size > 1

    val leftDropTargetNodeOption = if (shouldCreateDropTargetNodes) {
      val leftDropTargetNode = new DropTargetNode(this)
      leftDropTargetNode.setPrefWidth(3)
      leftDropTargetNode.setPrefHeight((nodeHeight / 4) * 3)
      leftDropTargetNode.setLayoutX(boundsForDropTarget.getMinX - 1)
      leftDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + (nodeHeight / 8))
      println("LEFT")
      Some(leftDropTargetNode)
    } else {
      None
    }

    val shouldCreateRightDropTargetNode = measureAreaTreeNode.getParent match {
      case measureAreaLayoutNode:MeasureAreaLayoutNode => {
        (shouldCreateDropTargetNodes && (measureAreaLayoutNode.getChildren.size == 1))
      }
      case _ => false
    }

    val rightDropTargetNodeOption = if (shouldCreateRightDropTargetNode) {
      val rightDropTargetNode = new DropTargetNode(this)
      rightDropTargetNode.setPrefWidth(3)
      rightDropTargetNode.setPrefHeight((nodeHeight / 4) * 3)
      rightDropTargetNode.setLayoutX(boundsForDropTarget.getMaxX - 2)
      rightDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + (nodeHeight / 8))
      println("RIGHT")
      Some(rightDropTargetNode)
    } else {
      None
    }

    List(leftDropTargetNodeOption, rightDropTargetNodeOption).flatten.map(_ -> None)
  }
  private def dropTargetsForMeasureAreaLayoutNode(measureAreaLayoutNode:MeasureAreaLayoutNode) = {
    val (nodeWidth, _) = nodeWidthAndHeight(measureAreaLayoutNode)
    val nodeSceneBounds = measureAreaLayoutNode.localToScene(measureAreaLayoutNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(nodeSceneBounds)

    val shouldCreateDropTargetNodes = measureAreaLayoutNode.getChildren.size > 1

    val topDropTargetNodeOption = if (shouldCreateDropTargetNodes) {
      val topDropTargetNode = new DropTargetNode(this)
      topDropTargetNode.setPrefWidth((nodeWidth / 4) * 3)
      topDropTargetNode.setPrefHeight(3)
      topDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + (nodeWidth / 8))
      topDropTargetNode.setLayoutY(boundsForDropTarget.getMinY - 1)
      println("TOP")
      Some(topDropTargetNode)
    } else {
      None
    }

    val shouldCreateBottomDropTargetNode = {
      shouldCreateDropTargetNodes && (measureAreaLayoutNode.getParent match {
        case measureAreaTreeNode:MeasureAreaTreeNode => {
          val measureAreaTreeNodeChildren = measureAreaTreeNode.getChildren
          ((measureAreaTreeNodeChildren.size == 2) && (measureAreaTreeNodeChildren.get(1) == measureAreaLayoutNode))
        }
        case _ => false
      })
    }

    val bottomDropTargetNodeOption = if (shouldCreateBottomDropTargetNode) {
      val bottomDropTargetNode = new DropTargetNode(this)
      bottomDropTargetNode.setPrefWidth((nodeWidth / 4) * 3)
      bottomDropTargetNode.setPrefHeight(3)
      bottomDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + (nodeWidth / 8))
      bottomDropTargetNode.setLayoutY(boundsForDropTarget.getMaxY - 2)
      println("BOTTOM")
      Some(bottomDropTargetNode)
    } else {
      None
    }

    List(topDropTargetNodeOption, bottomDropTargetNodeOption).flatten.map(_ -> None)
  }
}

class MeasureAreaLayoutNode(measureAreaLayout:MeasureAreaLayout, tableDataProperty:SimpleObjectProperty[TableData],
                            dragAndDrop:DragAndDrop, draggableParent:DraggableParent) extends HBox {
  val measureAreaTreeNodes = measureAreaLayout.measureAreaTrees.map(measureAreaTree => {
    new MeasureAreaTreeNode(measureAreaTree, tableDataProperty, dragAndDrop, draggableParent)
  })
  getChildren.addAll(measureAreaTreeNodes.toArray :_*)
}

class MeasureAreaTreeNode(measureAreaTree:MeasureAreaTree, tableDataProperty:SimpleObjectProperty[TableData],
                          dragAndDrop:DragAndDrop, draggableParent:DraggableParent) extends VBox {
  private val topNode = measureAreaTree.measureAreaTreeType match {
    case Left(measureAreaField) => new FieldNode(measureAreaField.field, dragAndDrop, draggableParent, tableDataProperty)
    case Right(measureAreaLayout) => new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, draggableParent)
  }
  getChildren.add(topNode)
  if (measureAreaTree.hasChildren) {
    val childMeasureLayoutNode = new MeasureAreaLayoutNode(measureAreaTree.childMeasureAreaLayout, tableDataProperty, dragAndDrop, draggableParent)
    getChildren.add(childMeasureLayoutNode)
  }
}