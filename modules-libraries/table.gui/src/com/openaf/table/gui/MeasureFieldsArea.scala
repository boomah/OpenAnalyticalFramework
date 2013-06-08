package com.openaf.table.gui

import com.openaf.table.api.{MeasureAreaLayout, TableData}
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends DragAndDropNode {
  getStyleClass.add("measure-fields-area")
  private val dropTargetsHelper = new MeasureFieldsAreaDropTargetsHelper(mainContent, dropTargetPane, this)

  def description = new SimpleStringProperty("Drop Measure and Column Fields Here")
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData.withMeasureAreaLayout(MeasureAreaLayout.fromFields(draggableFieldsInfo.draggable.fields))
  }
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetsHelper.dropTargetsToNodeSide(draggableFieldsInfo)
  def fields(tableDataOption:Option[TableData]) = measureAreaLayout(tableDataOption).allFields
  def nodes = List(new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, this))
  private def measureAreaLayout(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.measureAreaLayout
  }
  private def measureAreaLayout:MeasureAreaLayout = measureAreaLayout(None)
  private def parentMeasureAreaLayoutNode = mainContent.getChildren.get(0).asInstanceOf[MeasureAreaLayoutNode]
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val newMeasureAreaLayout = parentMeasureAreaLayoutNode.generateMeasureAreaLayoutWithRemoval(draggableFieldsInfo.draggable)
    val normalisedNewMeasureAreaLayout = newMeasureAreaLayout.normalise
    tableData.withMeasureAreaLayout(normalisedNewMeasureAreaLayout)
  }
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val newMeasureAreaLayout = if (tableData.tableState.tableLayout.measureAreaLayout.allFields.isEmpty) {
      MeasureAreaLayout.fromFields(draggableFieldsInfo.draggable.fields)
    } else {
      val nodeSide = dropTargetMap(dropTarget)
      parentMeasureAreaLayoutNode.generateMeasureAreaLayoutWithAddition(nodeSide, draggableFieldsInfo)
    }
    tableData.withMeasureAreaLayout(newMeasureAreaLayout)
  }
}