package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.{Priority, HBox}
import javafx.geometry.Side
import scala.collection.JavaConversions._
import com.openaf.table.lib.api._
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class MeasureAreaLayoutNode(measureAreaLayout:MeasureAreaLayout, tableDataProperty:SimpleObjectProperty[TableData],
                            dragAndDrop:DragAndDrop, dragAndDropContainer:DragAndDropContainer,
                            fieldBindings:ObservableMap[FieldID,StringBinding],
                            tableStateGeneratorProperty:SimpleObjectProperty[TableStateGenerator]) extends HBox {
  private val measureAreaTreeNodes = measureAreaLayout.measureAreaTrees.map(measureAreaTree => {
    val measureAreaTreeNode = new MeasureAreaTreeNode(measureAreaTree, tableDataProperty, dragAndDrop,
      dragAndDropContainer, fieldBindings, tableStateGeneratorProperty)
    HBox.setHgrow(measureAreaTreeNode, Priority.ALWAYS)
    measureAreaTreeNode
  })
  getChildren.addAll(measureAreaTreeNodes :_*)

  def childMeasureAreaTreeNodes = getChildren.collect{case (measureAreaTreeNode:MeasureAreaTreeNode) => measureAreaTreeNode}

  def generateMeasureAreaLayoutWithRemoval(draggableToRemove:Draggable) = {
    val measureAreaTrees = measureAreaTreeNodes.flatMap(_.generateWithRemovalOption(draggableToRemove))
    MeasureAreaLayout(measureAreaTrees)
  }

  def allFieldNodes:Seq[FieldNode] = measureAreaTreeNodes.flatMap(_.topFieldNodes)

  def generateMeasureAreaLayoutWithAddition(nodeSide:NodeSide, draggableFieldsInfo:DraggableFieldsInfo) = {
    val measureAreaTrees = measureAreaTreeNodes.flatMap(_.generateWithAdditionOption(nodeSide, draggableFieldsInfo))
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
          val newMeasureAreaTreeType = Right(MeasureAreaLayout(measureAreaTrees))
          val newChildMeasureAreaLayout = MeasureAreaLayout.fromFields(draggableFieldsInfo.draggable.fields)
          val newMeasureAreaTree = MeasureAreaTree(newMeasureAreaTreeType, newChildMeasureAreaLayout)
          MeasureAreaLayout(newMeasureAreaTree)
        }
        case unexpected => throw new IllegalStateException(s"A MeasureAreaLayoutNode should never have this side $unexpected")
      }
    } else {
      MeasureAreaLayout(measureAreaTrees)
    }
  }
}
