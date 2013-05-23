package com.openaf.table.gui

import javafx.scene.layout.Pane
import javafx.scene.Node
import javafx.geometry.Side
import scala.collection.JavaConversions._

class MeasureFieldsAreaDropTargetsHelper(mainContent:Pane, dropTargetPane:Pane, dropTargetContainer:DropTargetContainer) {
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

  private def parentMeasureAreaLayoutNode = mainContent.getChildren.get(0).asInstanceOf[MeasureAreaLayoutNode]

  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo):Map[DropTarget,NodeSide] = {
    val currentMeasureAreaLayoutNode = parentMeasureAreaLayoutNode
    allNodes(currentMeasureAreaLayoutNode).flatMap(node => {
      node match {
        case fieldNode:FieldNode if fieldNode != draggableFieldsInfo.draggable => dropTargetsForFieldNode(fieldNode, draggableFieldsInfo)
        case measureAreaTreeNode:MeasureAreaTreeNode => dropTargetsForMeasureAreaTreeNode(measureAreaTreeNode, draggableFieldsInfo)
        case measureAreaLayoutNode:MeasureAreaLayoutNode => dropTargetsForMeasureAreaLayoutNode(measureAreaLayoutNode, draggableFieldsInfo)
        case _ => Nil
      }
    }).toMap
  }

  private def nodeWidthAndHeight(node:Node) = (node.getLayoutBounds.getWidth, node.getLayoutBounds.getHeight)

  private def dropTargetsForFieldNode(fieldNode:FieldNode, draggableFieldsInfo:DraggableFieldsInfo) = {
    val parentMeasureAreaTreeNode = fieldNode.getParent.asInstanceOf[MeasureAreaTreeNode]
    val parentMeasureAreaLayoutNode = parentMeasureAreaTreeNode.getParent.asInstanceOf[MeasureAreaLayoutNode]
    val siblingMeasureAreaTreeNodes = parentMeasureAreaLayoutNode.childMeasureAreaTreeNodes
    val indexInSiblings = siblingMeasureAreaTreeNodes.indexOf(parentMeasureAreaTreeNode)
    val showLeftDropTargetNode = if (indexInSiblings == 0) {
      true
    } else {
      val measureAreaTreeNodeToTheLeft = siblingMeasureAreaTreeNodes(indexInSiblings - 1)
      val onlyOneFieldToTheLeft = (measureAreaTreeNodeToTheLeft.measureAreaTree.allFields.size == 1)
      if (onlyOneFieldToTheLeft) {
        (measureAreaTreeNodeToTheLeft.topFieldNodeOption.get != draggableFieldsInfo.draggable)
      } else {
        true
      }
    }
    val fieldIsAlone = (parentMeasureAreaTreeNode.measureAreaTree.allFields.size == 1)
    val moveLeftDropTargetNode = showLeftDropTargetNode && fieldIsAlone && {
      if (indexInSiblings == 0) {
        false
      } else {
        val measureAreaTreeNodeToTheLeft = siblingMeasureAreaTreeNodes(indexInSiblings - 1)
        val onlyOneFieldToTheLeft = (measureAreaTreeNodeToTheLeft.measureAreaTree.allFields.size == 1)
        onlyOneFieldToTheLeft
      }
    }
    val showRightDropTargetNode = !fieldIsAlone || {
      if (indexInSiblings == (siblingMeasureAreaTreeNodes.size - 1)) {
        true
      } else {
        val measureAreaTreeNodeToTheRight = siblingMeasureAreaTreeNodes(indexInSiblings + 1)
        val multipleFieldsToTheRight = (measureAreaTreeNodeToTheRight.measureAreaTree.allFields.size > 1)
        multipleFieldsToTheRight
      }
    }

    val (fieldNodeWidth, fieldNodeHeight) = nodeWidthAndHeight(fieldNode)
    val fieldNodeSceneBounds = fieldNode.localToScene(fieldNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(fieldNodeSceneBounds)

    val topDropTargetNode = new DropTargetNode(dropTargetContainer)
    val topX = boundsForDropTarget.getMinX + ((fieldNodeWidth / 2) - (topDropTargetNode.prefWidth(0) / 2))
    val topY = boundsForDropTarget.getMinY + (fieldNodeHeight / 4)
    topDropTargetNode.setLayoutX(topX)
    topDropTargetNode.setLayoutY(topY)
    val topDropTargetElement = (topDropTargetNode -> NodeSide(fieldNode, Side.TOP))

    val bottomDropTargetNode = new DropTargetNode(dropTargetContainer)
    val bottomX = boundsForDropTarget.getMinX + ((fieldNodeWidth / 2) - (bottomDropTargetNode.prefWidth(0) / 2))
    val bottomY = boundsForDropTarget.getMinY + (((fieldNodeHeight / 4) * 3) - bottomDropTargetNode.prefHeight(0))
    bottomDropTargetNode.setLayoutX(bottomX)
    bottomDropTargetNode.setLayoutY(bottomY)
    val bottomDropTargetElement = (bottomDropTargetNode -> NodeSide(fieldNode, Side.BOTTOM))

    val leftDropTargetElementOption = if (showLeftDropTargetNode) {
      val leftDropTargetNode = new DropTargetNode(dropTargetContainer)
      val xDelta = -(leftDropTargetNode.prefWidth(0) / 2)
      val leftX = boundsForDropTarget.getMinX + (if (moveLeftDropTargetNode) xDelta else (fieldNodeWidth / 4))
      val leftY = boundsForDropTarget.getMinY + ((fieldNodeHeight / 2) - (leftDropTargetNode.prefHeight(0) / 2))
      leftDropTargetNode.setLayoutX(leftX)
      leftDropTargetNode.setLayoutY(leftY)
      Some((leftDropTargetNode -> NodeSide(fieldNode, Side.LEFT)))
    } else {
      None
    }

    val rightDropTargetElementOption = if (showRightDropTargetNode) {
      val rightDropTargetNode = new DropTargetNode(dropTargetContainer)
      val rightX = boundsForDropTarget.getMinX + (((fieldNodeWidth / 4) * 3) - rightDropTargetNode.prefWidth(0))
      val rightY = boundsForDropTarget.getMinY + ((fieldNodeHeight / 2) - (rightDropTargetNode.prefHeight(0) / 2))
      rightDropTargetNode.setLayoutX(rightX)
      rightDropTargetNode.setLayoutY(rightY)
      Some((rightDropTargetNode -> NodeSide(fieldNode, Side.RIGHT)))
    } else {
      None
    }

    List(Some(topDropTargetElement), Some(bottomDropTargetElement), leftDropTargetElementOption, rightDropTargetElementOption).flatten
  }

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
      val leftDropTargetNode = new DropTargetNode(dropTargetContainer)
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
      val rightDropTargetNode = new DropTargetNode(dropTargetContainer)
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

  private def dropTargetsForMeasureAreaLayoutNode(measureAreaLayoutNode:MeasureAreaLayoutNode, draggableFieldsInfo:DraggableFieldsInfo) = {
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
      val topDropTargetNode = new DropTargetNode(dropTargetContainer)
      topDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + ((nodeWidth / 2) - (topDropTargetNode.prefWidth(0) / 2)))
      val yDelta = if (multipleChildrenAbove) (topDropTargetNode.prefHeight(0) / 2) else 0
      topDropTargetNode.setLayoutY(boundsForDropTarget.getMinY - yDelta)
      println("TOP")
      Some(topDropTargetNode -> NodeSide(measureAreaLayoutNode, Side.TOP))
    } else {
      None
    }

    val shouldCreateBottomDropTargetNode = {
      multipleChildren && {
        val topMeasureAreaLayoutNode = parentMeasureAreaLayoutNode
        val measureAreaTreeNodes = topMeasureAreaLayoutNode.getChildren.collect{case (measureAreaTreeNode:MeasureAreaTreeNode) => measureAreaTreeNode}
        (measureAreaLayoutNode == topMeasureAreaLayoutNode) ||
          measureAreaTreeNodes.exists(measureAreaTreeNode => {
            val measureAreaTreeNodeChildren = measureAreaTreeNode.getChildren
            ((measureAreaTreeNodeChildren.size == 2) && (measureAreaTreeNodeChildren.get(1) == measureAreaLayoutNode))
          })
      }
    }

    val bottomDropTargetNodeOption = if (shouldCreateBottomDropTargetNode) {
      val bottomDropTargetNode = new DropTargetNode(dropTargetContainer)
      bottomDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + ((nodeWidth / 2) - (bottomDropTargetNode.prefWidth(0) / 2)))
      bottomDropTargetNode.setLayoutY(boundsForDropTarget.getMaxY - bottomDropTargetNode.prefHeight(0))
      println("BOTTOM")
      Some(bottomDropTargetNode -> NodeSide(measureAreaLayoutNode, Side.BOTTOM))
    } else {
      None
    }

    List(topDropTargetNodeOption, bottomDropTargetNodeOption).flatten
  }
}
