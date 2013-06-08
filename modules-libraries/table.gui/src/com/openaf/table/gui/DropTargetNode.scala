package com.openaf.table.gui

import javafx.scene.layout.FlowPane
import com.openaf.table.api.TableData
import javafx.scene.control.Label

class DropTargetNode(dropTargetContainer:DropTargetContainer) extends FlowPane with DropTarget {
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    dropTargetContainer.childFieldsDropped(this, draggableFieldsInfo, tableData)
  }
  getStyleClass.add("drop-target-node")
  setPrefWidth(DropTargetNode.size)
  setPrefHeight(DropTargetNode.size)
}

object DropTargetNode {
  val size = 3

  def createDropTargetNodesForLabel(label:Label, dropTargetContainer:DropTargetContainer) = {
    val dropTarget1 = new DropTargetNode(dropTargetContainer)
    dropTarget1.layoutXProperty.bind(label.widthProperty.divide(4).subtract(dropTarget1.widthProperty.divide(2)))
    dropTarget1.layoutYProperty.bind(label.heightProperty.divide(2).subtract(dropTarget1.heightProperty.divide(2)))

    val dropTarget2 = new DropTargetNode(dropTargetContainer)
    dropTarget2.layoutXProperty.bind(label.widthProperty.divide(4).multiply(3).subtract(dropTarget2.widthProperty.divide(2)))
    dropTarget2.layoutYProperty.bind(label.heightProperty.divide(2).subtract(dropTarget2.heightProperty.divide(2)))

    (dropTarget1, dropTarget2)
  }
}