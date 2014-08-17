package com.openaf.table.gui

import javafx.geometry.Side
import javafx.scene.layout.Pane

class FieldNodeDropTargetsHelper(dropTargetPane:Pane, dragAndDropContainer:DragAndDropContainer) {
  def dropTargetsForFieldNode(fieldNode:FieldNode[_], draggableFieldsInfo:DraggableFieldsInfo) = {
    val parentColumnAreaTreeNode = fieldNode.getParent.asInstanceOf[ColumnHeaderTreeNode]
    val parentColumnHeaderLayoutNode = parentColumnAreaTreeNode.getParent.asInstanceOf[ColumnHeaderLayoutNode]

    val outerNode = parentColumnHeaderLayoutNode.getParent
    val showTopDropTargetNode = outerNode match {
      case outerColumnAreaTreeNode:ColumnHeaderTreeNode => {
        val indexInOuterColumnAreaTreeNode = outerColumnAreaTreeNode.getChildren.indexOf(parentColumnHeaderLayoutNode)
        if (indexInOuterColumnAreaTreeNode == 1) {
          outerColumnAreaTreeNode.topFieldNodeOption match {
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
      case outerColumnAreaTreeNode:ColumnHeaderTreeNode => {
        val indexInOuterColumnAreaTreeNode = outerColumnAreaTreeNode.getChildren.indexOf(parentColumnHeaderLayoutNode)
        val bottomInTree = (indexInOuterColumnAreaTreeNode == 1)
        bottomInTree && (outerColumnAreaTreeNode.topFieldNodes.size == 1) && (parentColumnHeaderLayoutNode.childColumnAreaTreeNodes.size == 1)
      }
      case _ => false
    })
    val showBottomDropTargetNode = {
      val columnHeaderTreesBelow = parentColumnAreaTreeNode.columnHeaderTree.childColumnHeaderLayout.columnHeaderTrees
      columnHeaderTreesBelow match {
        case columnHeaderTreeBelow :: Nil if columnHeaderTreeBelow.columnHeaderTreeType.isLeft => false
        case _ => true
      }
    }

    val siblingColumnAreaTreeNodes = parentColumnHeaderLayoutNode.childColumnAreaTreeNodes
    val indexInSiblings = siblingColumnAreaTreeNodes.indexOf(parentColumnAreaTreeNode)
    val showLeftDropTargetNode = if (indexInSiblings == 0) {
      true
    } else {
      val columnHeaderTreeNodeToTheLeft = siblingColumnAreaTreeNodes(indexInSiblings - 1)
      val onlyOneFieldToTheLeft = (columnHeaderTreeNodeToTheLeft.columnHeaderTree.allFields.size == 1)
      if (onlyOneFieldToTheLeft) {
        (columnHeaderTreeNodeToTheLeft.topFieldNodeOption.get != draggableFieldsInfo.draggable)
      } else {
        true
      }
    }
    val fieldIsAlone = (parentColumnAreaTreeNode.columnHeaderTree.allFields.size == 1)
    val moveLeftDropTargetNode = showLeftDropTargetNode && fieldIsAlone && {
      if (indexInSiblings == 0) {
        false
      } else {
        val columnHeaderTreeNodeToTheLeft = siblingColumnAreaTreeNodes(indexInSiblings - 1)
        val onlyOneFieldToTheLeft = (columnHeaderTreeNodeToTheLeft.columnHeaderTree.allFields.size == 1)
        onlyOneFieldToTheLeft
      }
    }
    val showRightDropTargetNode = !fieldIsAlone || {
      if (indexInSiblings == (siblingColumnAreaTreeNodes.size - 1)) {
        true
      } else {
        val columnHeaderTreeNodeToTheRight = siblingColumnAreaTreeNodes(indexInSiblings + 1)
        val multipleFieldsToTheRight = (columnHeaderTreeNodeToTheRight.columnHeaderTree.allFields.size > 1)
        multipleFieldsToTheRight
      }
    }

    val (fieldNodeWidth, fieldNodeHeight) = (fieldNode.getLayoutBounds.getWidth, fieldNode.getLayoutBounds.getHeight)
    val fieldNodeSceneBounds = fieldNode.localToScene(fieldNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(fieldNodeSceneBounds)

    val topDropTargetElementOption = if (showTopDropTargetNode) {
      val topDropTargetNode = new DropTargetNode(dragAndDropContainer)
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
      val bottomDropTargetNode = new DropTargetNode(dragAndDropContainer)
      val bottomX = boundsForDropTarget.getMinX + ((fieldNodeWidth / 2) - (bottomDropTargetNode.prefWidth(0) / 2))
      val bottomY = boundsForDropTarget.getMinY + (((fieldNodeHeight / 4) * 3) - bottomDropTargetNode.prefHeight(0))
      bottomDropTargetNode.setLayoutX(bottomX)
      bottomDropTargetNode.setLayoutY(bottomY)
      Some(bottomDropTargetNode -> NodeSide(fieldNode, Side.BOTTOM))
    } else {
      None
    }

    val leftDropTargetElementOption = if (showLeftDropTargetNode) {
      val leftDropTargetNode = new DropTargetNode(dragAndDropContainer)
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
      val rightDropTargetNode = new DropTargetNode(dragAndDropContainer)
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
