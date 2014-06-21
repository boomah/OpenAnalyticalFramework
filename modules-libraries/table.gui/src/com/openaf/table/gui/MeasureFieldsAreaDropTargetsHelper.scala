package com.openaf.table.gui

import javafx.scene.layout.Pane
import javafx.scene.Node
import javafx.geometry.Side
import scala.collection.JavaConversions._
import javafx.scene.control.Label

class MeasureFieldsAreaDropTargetsHelper(mainContent:Pane, dropTargetPane:Pane, dragAndDropContainer:DragAndDropContainer) {
  private val fieldNodeDropTargetsHelper = new FieldNodeDropTargetsHelper(dropTargetPane, dragAndDropContainer)

  private def allNodes(pane:Pane):List[Node] = {
    val paneChildNodes = pane.getChildren.toList
    val deepChildNodes = paneChildNodes.flatMap(nodeAny => {
      nodeAny match {
        case childPane:Pane => allNodes(childPane)
        case node:Node => List(node)
      }
    })
    pane :: deepChildNodes
  }

  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo):Map[DropTarget,NodeSide] = {
    mainContent.getChildren.get(0) match {
      case label:Label => {
        val (dropTarget1, dropTarget2) = DropTargetNode.createDropTargetNodesForLabel(label, dragAndDropContainer)
        Map(dropTarget1 -> NodeSide(label, Side.LEFT), dropTarget2 -> NodeSide(label, Side.LEFT))
      }
      case currentMeasureAreaLayoutNode:MeasureAreaLayoutNode => {
        allNodes(currentMeasureAreaLayoutNode).flatMap(node => {
          node match {
            case fieldNode:FieldNode if fieldNode != draggableFieldsInfo.draggable => fieldNodeDropTargetsHelper.dropTargetsForFieldNode(fieldNode, draggableFieldsInfo)
            case measureAreaTreeNode:MeasureAreaTreeNode => dropTargetsForMeasureAreaTreeNode(measureAreaTreeNode, draggableFieldsInfo)
            case measureAreaLayoutNode:MeasureAreaLayoutNode => dropTargetsForMeasureAreaLayoutNode(measureAreaLayoutNode, draggableFieldsInfo, currentMeasureAreaLayoutNode)
            case _ => Nil
          }
        }).toMap
      }
    }
  }

  private def nodeWidthAndHeight(node:Node) = (node.getLayoutBounds.getWidth, node.getLayoutBounds.getHeight)

  private def dropTargetsForMeasureAreaTreeNode(measureAreaTreeNode:MeasureAreaTreeNode, draggableFieldsInfo:DraggableFieldsInfo) = {
    val (_, nodeHeight) = nodeWidthAndHeight(measureAreaTreeNode)
    val nodeSceneBounds = measureAreaTreeNode.localToScene(measureAreaTreeNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(nodeSceneBounds)

    val shouldCreateDropTargetNodes = measureAreaTreeNode.containsTopAndChildFieldNodes(draggableFieldsInfo.draggable)
    val parentMeasureAreaLayoutNode = measureAreaTreeNode.getParent.asInstanceOf[MeasureAreaLayoutNode]

    val shouldCreateLeftDropTargetNode = shouldCreateDropTargetNodes && {
      val indexInParent = parentMeasureAreaLayoutNode.getChildren.indexOf(measureAreaTreeNode)
      (indexInParent == 0) || (indexInParent > 0) && {
        val leftMeasureAreaTreeNode = parentMeasureAreaLayoutNode.getChildren.get(indexInParent - 1).asInstanceOf[MeasureAreaTreeNode]
        leftMeasureAreaTreeNode.containsTopAndChildFieldNodes(draggableFieldsInfo.draggable)
      }
    }

    val leftDropTargetNodeOption = if (shouldCreateLeftDropTargetNode) {
      val leftDropTargetNode = new DropTargetNode(dragAndDropContainer)
      val isFirstMeasureAreaTreeNodeInParent = (parentMeasureAreaLayoutNode.getChildren.get(0) == measureAreaTreeNode)
      val xDelta = if (isFirstMeasureAreaTreeNodeInParent) 0 else (leftDropTargetNode.prefWidth(0) / 2)
      leftDropTargetNode.setLayoutX(boundsForDropTarget.getMinX - xDelta)
      leftDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + ((nodeHeight / 2) - (leftDropTargetNode.prefHeight(0) / 2)))
      Some(leftDropTargetNode -> NodeSide(measureAreaTreeNode, Side.LEFT))
    } else {
      None
    }

    val shouldCreateRightDropTargetNode = shouldCreateDropTargetNodes &&
      (parentMeasureAreaLayoutNode.getChildren.get(parentMeasureAreaLayoutNode.getChildren.size - 1) == measureAreaTreeNode)

    val rightDropTargetNodeOption = if (shouldCreateRightDropTargetNode) {
      val rightDropTargetNode = new DropTargetNode(dragAndDropContainer)
      rightDropTargetNode.setLayoutX(boundsForDropTarget.getMaxX - rightDropTargetNode.prefWidth(0))
      rightDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + ((nodeHeight / 2) - (rightDropTargetNode.prefHeight(0) / 2)))
      Some(rightDropTargetNode -> NodeSide(measureAreaTreeNode, Side.RIGHT))
    } else {
      None
    }

    List(leftDropTargetNodeOption, rightDropTargetNodeOption).flatten
  }

  private def topLevelParent(measureAreaLayoutNode:MeasureAreaLayoutNode):Boolean = {
    measureAreaLayoutNode.getParent match {
      case measureAreaTreeNode:MeasureAreaTreeNode => {
        if (measureAreaTreeNode.topMeasureAreaLayoutNodeOption == Some(measureAreaLayoutNode)) {
          measureAreaTreeNode.getParent match {
            case parentMeasureAreaLayoutNode:MeasureAreaLayoutNode => topLevelParent(parentMeasureAreaLayoutNode)
            case unexpected => throw new IllegalStateException(s"MeasureAreaTreeNode parent should always be MeasureAreaLayoutNode $unexpected")
          }
        } else {
          false
        }
      }
      case `mainContent` => true
    }
  }

  private def dropTargetsForMeasureAreaLayoutNode(measureAreaLayoutNode:MeasureAreaLayoutNode, draggableFieldsInfo:DraggableFieldsInfo,
                                                  topMeasureAreaLayoutNode:MeasureAreaLayoutNode) = {
    val (nodeWidth, _) = nodeWidthAndHeight(measureAreaLayoutNode)
    val nodeSceneBounds = measureAreaLayoutNode.localToScene(measureAreaLayoutNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(nodeSceneBounds)

    val measureAreaLayoutNodeParent = measureAreaLayoutNode.getParent
    val isTopLevelParent = topLevelParent(measureAreaLayoutNode)
    val filteredChildren = measureAreaLayoutNode.childMeasureAreaTreeNodes.filterNot(_.topFieldNodeOption == Some(draggableFieldsInfo.draggable))
    val multipleChildren = (filteredChildren.size > 1)
    val multipleChildrenAbove = (!isTopLevelParent && {
      val parentMeasureAreaTreeNode = measureAreaLayoutNodeParent.asInstanceOf[MeasureAreaTreeNode]
      val children = parentMeasureAreaTreeNode.getChildren
      ((children.size == 2) && (children.get(1) == measureAreaLayoutNode) && !children.get(0).isInstanceOf[FieldNode])
    })
    val shouldCreateTopDropTargetNode = (multipleChildren && (isTopLevelParent || multipleChildrenAbove))

    val topDropTargetNodeOption = if (shouldCreateTopDropTargetNode) {
      val topDropTargetNode = new DropTargetNode(dragAndDropContainer)
      topDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + ((nodeWidth / 2) - (topDropTargetNode.prefWidth(0) / 2)))
      val yDelta = if (multipleChildrenAbove) (topDropTargetNode.prefHeight(0) / 2) else 0
      topDropTargetNode.setLayoutY(boundsForDropTarget.getMinY - yDelta)
      Some(topDropTargetNode -> NodeSide(measureAreaLayoutNode, Side.TOP))
    } else {
      None
    }

    val shouldCreateBottomDropTargetNode = {
      multipleChildren && {
        val measureAreaTreeNodes = topMeasureAreaLayoutNode.getChildren.collect{case (measureAreaTreeNode:MeasureAreaTreeNode) => measureAreaTreeNode}
        (measureAreaLayoutNode == topMeasureAreaLayoutNode) ||
          measureAreaTreeNodes.exists(measureAreaTreeNode => {
            val measureAreaTreeNodeChildren = measureAreaTreeNode.getChildren
            ((measureAreaTreeNodeChildren.size == 2) && (measureAreaTreeNodeChildren.get(1) == measureAreaLayoutNode))
          })
      }
    }

    val bottomDropTargetNodeOption = if (shouldCreateBottomDropTargetNode) {
      val bottomDropTargetNode = new DropTargetNode(dragAndDropContainer)
      bottomDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + ((nodeWidth / 2) - (bottomDropTargetNode.prefWidth(0) / 2)))
      bottomDropTargetNode.setLayoutY(boundsForDropTarget.getMaxY - bottomDropTargetNode.prefHeight(0))
      Some(bottomDropTargetNode -> NodeSide(measureAreaLayoutNode, Side.BOTTOM))
    } else {
      None
    }

    List(topDropTargetNodeOption, bottomDropTargetNodeOption).flatten
  }
}
