package com.openaf.table.gui

import javafx.scene.layout.FlowPane
import javafx.scene.control.Label
import com.openaf.table.lib.api.TableState

class DropTargetNode(dragAndDropContainer:DragAndDropContainer) extends FlowPane with DropTarget {
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableState:TableState) = {
    dragAndDropContainer.childFieldsDropped(this, draggableFieldsInfo, tableState)
  }
  getStyleClass.add("drop-target-node")
  setPrefWidth(DropTargetNode.size)
  setPrefHeight(DropTargetNode.size)
}

object DropTargetNode {
  val size = 3

  def createDropTargetNodesForLabel(label:Label, dragAndDropContainer:DragAndDropContainer) = {
    val dropTarget1 = new DropTargetNode(dragAndDropContainer)
    dropTarget1.layoutXProperty.bind(label.widthProperty.divide(4).subtract(dropTarget1.widthProperty.divide(2)))
    dropTarget1.layoutYProperty.bind(label.heightProperty.divide(2).subtract(dropTarget1.heightProperty.divide(2)))

    val dropTarget2 = new DropTargetNode(dragAndDropContainer)
    dropTarget2.layoutXProperty.bind(label.widthProperty.divide(4).multiply(3).subtract(dropTarget2.widthProperty.divide(2)))
    dropTarget2.layoutYProperty.bind(label.heightProperty.divide(2).subtract(dropTarget2.heightProperty.divide(2)))

    (dropTarget1, dropTarget2)
  }
}