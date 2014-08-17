package com.openaf.table.gui

import javafx.scene.layout.Pane
import javafx.scene.Node
import javafx.geometry.Side
import scala.collection.JavaConversions._
import javafx.scene.control.Label

class ColumnHeaderAreaDropTargetsHelper(mainContent:Pane, dropTargetPane:Pane, dragAndDropContainer:DragAndDropContainer) {
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
      case currentColumnHeaderLayoutNode:ColumnHeaderLayoutNode => {
        allNodes(currentColumnHeaderLayoutNode).flatMap(node => {
          node match {
            case fieldNode:FieldNode[_] if fieldNode != draggableFieldsInfo.draggable => fieldNodeDropTargetsHelper.dropTargetsForFieldNode(fieldNode, draggableFieldsInfo)
            case columnHeaderTreeNode:ColumnHeaderTreeNode => dropTargetsForColumnHeaderTreeNode(columnHeaderTreeNode, draggableFieldsInfo)
            case columnHeaderLayoutNode:ColumnHeaderLayoutNode => dropTargetsForColumnHeaderLayoutNode(columnHeaderLayoutNode, draggableFieldsInfo, currentColumnHeaderLayoutNode)
            case _ => Nil
          }
        }).toMap
      }
    }
  }

  private def nodeWidthAndHeight(node:Node) = (node.getLayoutBounds.getWidth, node.getLayoutBounds.getHeight)

  private def dropTargetsForColumnHeaderTreeNode(columnHeaderTreeNode:ColumnHeaderTreeNode, draggableFieldsInfo:DraggableFieldsInfo) = {
    val (_, nodeHeight) = nodeWidthAndHeight(columnHeaderTreeNode)
    val nodeSceneBounds = columnHeaderTreeNode.localToScene(columnHeaderTreeNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(nodeSceneBounds)

    val shouldCreateDropTargetNodes = columnHeaderTreeNode.containsTopAndChildFieldNodes(draggableFieldsInfo.draggable)
    val parentColumnHeaderLayoutNode = columnHeaderTreeNode.getParent.asInstanceOf[ColumnHeaderLayoutNode]

    val shouldCreateLeftDropTargetNode = shouldCreateDropTargetNodes && {
      val indexInParent = parentColumnHeaderLayoutNode.getChildren.indexOf(columnHeaderTreeNode)
      (indexInParent == 0) || (indexInParent > 0) && {
        val leftColumnHeaderTreeNode = parentColumnHeaderLayoutNode.getChildren.get(indexInParent - 1).asInstanceOf[ColumnHeaderTreeNode]
        leftColumnHeaderTreeNode.containsTopAndChildFieldNodes(draggableFieldsInfo.draggable)
      }
    }

    val leftDropTargetNodeOption = if (shouldCreateLeftDropTargetNode) {
      val leftDropTargetNode = new DropTargetNode(dragAndDropContainer)
      val isFirstColumnHeaderTreeNodeInParent = (parentColumnHeaderLayoutNode.getChildren.get(0) == columnHeaderTreeNode)
      val xDelta = if (isFirstColumnHeaderTreeNodeInParent) 0 else (leftDropTargetNode.prefWidth(0) / 2)
      leftDropTargetNode.setLayoutX(boundsForDropTarget.getMinX - xDelta)
      leftDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + ((nodeHeight / 2) - (leftDropTargetNode.prefHeight(0) / 2)))
      Some(leftDropTargetNode -> NodeSide(columnHeaderTreeNode, Side.LEFT))
    } else {
      None
    }

    val shouldCreateRightDropTargetNode = shouldCreateDropTargetNodes &&
      (parentColumnHeaderLayoutNode.getChildren.get(parentColumnHeaderLayoutNode.getChildren.size - 1) == columnHeaderTreeNode)

    val rightDropTargetNodeOption = if (shouldCreateRightDropTargetNode) {
      val rightDropTargetNode = new DropTargetNode(dragAndDropContainer)
      rightDropTargetNode.setLayoutX(boundsForDropTarget.getMaxX - rightDropTargetNode.prefWidth(0))
      rightDropTargetNode.setLayoutY(boundsForDropTarget.getMinY + ((nodeHeight / 2) - (rightDropTargetNode.prefHeight(0) / 2)))
      Some(rightDropTargetNode -> NodeSide(columnHeaderTreeNode, Side.RIGHT))
    } else {
      None
    }

    List(leftDropTargetNodeOption, rightDropTargetNodeOption).flatten
  }

  private def topLevelParent(columnHeaderLayoutNode:ColumnHeaderLayoutNode):Boolean = {
    columnHeaderLayoutNode.getParent match {
      case columnHeaderTreeNode:ColumnHeaderTreeNode => {
        if (columnHeaderTreeNode.topColumnHeaderLayoutNodeOption == Some(columnHeaderLayoutNode)) {
          columnHeaderTreeNode.getParent match {
            case parentColumnHeaderLayoutNode:ColumnHeaderLayoutNode => topLevelParent(parentColumnHeaderLayoutNode)
            case unexpected => throw new IllegalStateException(s"ColumnAreaTreeNode parent should always be ColumnHeaderLayoutNode $unexpected")
          }
        } else {
          false
        }
      }
      case `mainContent` => true
    }
  }

  private def dropTargetsForColumnHeaderLayoutNode(columnHeaderLayoutNode:ColumnHeaderLayoutNode, draggableFieldsInfo:DraggableFieldsInfo,
                                                  topColumnHeaderLayoutNode:ColumnHeaderLayoutNode) = {
    val (nodeWidth, _) = nodeWidthAndHeight(columnHeaderLayoutNode)
    val nodeSceneBounds = columnHeaderLayoutNode.localToScene(columnHeaderLayoutNode.getBoundsInLocal)
    val boundsForDropTarget = dropTargetPane.sceneToLocal(nodeSceneBounds)

    val columnHeaderLayoutNodeParent = columnHeaderLayoutNode.getParent
    val isTopLevelParent = topLevelParent(columnHeaderLayoutNode)
    val filteredChildren = columnHeaderLayoutNode.childColumnAreaTreeNodes.filterNot(_.topFieldNodeOption == Some(draggableFieldsInfo.draggable))
    val multipleChildren = (filteredChildren.size > 1)
    val multipleChildrenAbove = (!isTopLevelParent && {
      val parentColumnAreaTreeNode = columnHeaderLayoutNodeParent.asInstanceOf[ColumnHeaderTreeNode]
      val children = parentColumnAreaTreeNode.getChildren
      ((children.size == 2) && (children.get(1) == columnHeaderLayoutNode) && !children.get(0).isInstanceOf[FieldNode[_]])
    })
    val shouldCreateTopDropTargetNode = (multipleChildren && (isTopLevelParent || multipleChildrenAbove))

    val topDropTargetNodeOption = if (shouldCreateTopDropTargetNode) {
      val topDropTargetNode = new DropTargetNode(dragAndDropContainer)
      topDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + ((nodeWidth / 2) - (topDropTargetNode.prefWidth(0) / 2)))
      val yDelta = if (multipleChildrenAbove) (topDropTargetNode.prefHeight(0) / 2) else 0
      topDropTargetNode.setLayoutY(boundsForDropTarget.getMinY - yDelta)
      Some(topDropTargetNode -> NodeSide(columnHeaderLayoutNode, Side.TOP))
    } else {
      None
    }

    val shouldCreateBottomDropTargetNode = {
      multipleChildren && {
        val columnHeaderTreeNodes = topColumnHeaderLayoutNode.getChildren.collect{case (columnHeaderTreeNode:ColumnHeaderTreeNode) => columnHeaderTreeNode}
        (columnHeaderLayoutNode == topColumnHeaderLayoutNode) ||
          columnHeaderTreeNodes.exists(columnHeaderTreeNode => {
            val columnHeaderTreeNodeChildren = columnHeaderTreeNode.getChildren
            ((columnHeaderTreeNodeChildren.size == 2) && (columnHeaderTreeNodeChildren.get(1) == columnHeaderLayoutNode))
          })
      }
    }

    val bottomDropTargetNodeOption = if (shouldCreateBottomDropTargetNode) {
      val bottomDropTargetNode = new DropTargetNode(dragAndDropContainer)
      bottomDropTargetNode.setLayoutX(boundsForDropTarget.getMinX + ((nodeWidth / 2) - (bottomDropTargetNode.prefWidth(0) / 2)))
      bottomDropTargetNode.setLayoutY(boundsForDropTarget.getMaxY - bottomDropTargetNode.prefHeight(0))
      Some(bottomDropTargetNode -> NodeSide(columnHeaderLayoutNode, Side.BOTTOM))
    } else {
      None
    }

    List(topDropTargetNodeOption, bottomDropTargetNodeOption).flatten
  }
}
