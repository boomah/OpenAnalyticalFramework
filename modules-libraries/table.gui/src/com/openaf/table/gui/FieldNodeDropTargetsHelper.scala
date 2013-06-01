package com.openaf.table.gui

import javafx.geometry.Side
import javafx.scene.layout.Pane

class FieldNodeDropTargetsHelper(dropTargetPane:Pane, dropTargetContainer:DropTargetContainer) {
  def dropTargetsForFieldNode(fieldNode:FieldNode, draggableFieldsInfo:DraggableFieldsInfo) = {
    val parentMeasureAreaTreeNode = fieldNode.getParent.asInstanceOf[MeasureAreaTreeNode]
    val parentMeasureAreaLayoutNode = parentMeasureAreaTreeNode.getParent.asInstanceOf[MeasureAreaLayoutNode]

    val outerNode = parentMeasureAreaLayoutNode.getParent
    val showTopDropTargetNode = outerNode match {
      case outerMeasureAreaTreeNode:MeasureAreaTreeNode => {
        val indexInOuterMeasureAreaTreeNode = outerMeasureAreaTreeNode.getChildren.indexOf(parentMeasureAreaLayoutNode)
        if (indexInOuterMeasureAreaTreeNode == 1) {
          outerMeasureAreaTreeNode.topFieldNodeOption match {
            case Some(topFieldNode) if topFieldNode == draggableFieldsInfo.draggable => false
            case _ => true
          }
        } else {
          true
        }
      }
      case _ => true
    }
    val moveTopDropTargetNode = showTopDropTargetNode && (outerNode match {
      case outerMeasureAreaTreeNode:MeasureAreaTreeNode => {
        val indexInOuterMeasureAreaTreeNode = outerMeasureAreaTreeNode.getChildren.indexOf(parentMeasureAreaLayoutNode)
        (indexInOuterMeasureAreaTreeNode == 1)
      }
      case _ => false
    })
    val showBottomDropTargetNode = {
      val measureAreaTreesBelow = parentMeasureAreaTreeNode.measureAreaTree.childMeasureAreaLayout.measureAreaTrees
      measureAreaTreesBelow match {
        case measureAreaTreeBelow :: Nil if measureAreaTreeBelow.measureAreaTreeType.isLeft => false
        case _ => true
      }
    }

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

    val (fieldNodeWidth, fieldNodeHeight) = (fieldNode.getLayoutBounds.getWidth, fieldNode.getLayoutBounds.getHeight)
    val fieldNodeSceneBounds = fieldNode.localToScene(fieldNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(fieldNodeSceneBounds)

    val topDropTargetElementOption = if (showTopDropTargetNode) {
      val topDropTargetNode = new DropTargetNode(dropTargetContainer)
      val yDelta = -(topDropTargetNode.prefWidth(0) / 2)
      val topX = boundsForDropTarget.getMinX + ((fieldNodeWidth / 2) - (topDropTargetNode.prefWidth(0) / 2))
      val topY = boundsForDropTarget.getMinY + (if (moveTopDropTargetNode) yDelta else (fieldNodeHeight / 4))
      topDropTargetNode.setLayoutX(topX)
      topDropTargetNode.setLayoutY(topY)
      Some(topDropTargetNode -> NodeSide(fieldNode, Side.TOP))
    } else {
      None
    }

    val bottomDropTargetElementOption = if (showBottomDropTargetNode ) {
      val bottomDropTargetNode = new DropTargetNode(dropTargetContainer)
      val bottomX = boundsForDropTarget.getMinX + ((fieldNodeWidth / 2) - (bottomDropTargetNode.prefWidth(0) / 2))
      val bottomY = boundsForDropTarget.getMinY + (((fieldNodeHeight / 4) * 3) - bottomDropTargetNode.prefHeight(0))
      bottomDropTargetNode.setLayoutX(bottomX)
      bottomDropTargetNode.setLayoutY(bottomY)
      Some(bottomDropTargetNode -> NodeSide(fieldNode, Side.BOTTOM))
    } else {
      None
    }

    val leftDropTargetElementOption = if (showLeftDropTargetNode) {
      val leftDropTargetNode = new DropTargetNode(dropTargetContainer)
      val xDelta = -(leftDropTargetNode.prefWidth(0) / 2)
      val leftX = boundsForDropTarget.getMinX + (if (moveLeftDropTargetNode) xDelta else (fieldNodeWidth / 4))
      val leftY = boundsForDropTarget.getMinY + ((fieldNodeHeight / 2) - (leftDropTargetNode.prefHeight(0) / 2))
      leftDropTargetNode.setLayoutX(leftX)
      leftDropTargetNode.setLayoutY(leftY)
      Some(leftDropTargetNode -> NodeSide(fieldNode, Side.LEFT))
    } else {
      None
    }

    val rightDropTargetElementOption = if (showRightDropTargetNode) {
      val rightDropTargetNode = new DropTargetNode(dropTargetContainer)
      val rightX = boundsForDropTarget.getMinX + (((fieldNodeWidth / 4) * 3) - rightDropTargetNode.prefWidth(0))
      val rightY = boundsForDropTarget.getMinY + ((fieldNodeHeight / 2) - (rightDropTargetNode.prefHeight(0) / 2))
      rightDropTargetNode.setLayoutX(rightX)
      rightDropTargetNode.setLayoutY(rightY)
      Some(rightDropTargetNode -> NodeSide(fieldNode, Side.RIGHT))
    } else {
      None
    }

    List(topDropTargetElementOption, bottomDropTargetElementOption, leftDropTargetElementOption, rightDropTargetElementOption).flatten
  }
}
