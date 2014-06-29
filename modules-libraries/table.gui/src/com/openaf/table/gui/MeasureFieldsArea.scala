package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{TableStateGenerator, FieldID, TableData, MeasureAreaLayout}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                        val locale:SimpleObjectProperty[Locale],
                        fieldBindings:ObservableMap[FieldID,StringBinding],
                        tableStateGeneratorProperty:SimpleObjectProperty[TableStateGenerator])
  extends DragAndDropContainerNode {

  getStyleClass.add("measure-fields-area")
  private val dropTargetsHelper = new MeasureFieldsAreaDropTargetsHelper(mainContent, dropTargetPane, this)

  def descriptionID = "measureDescription"
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData.withMeasureAreaLayout(MeasureAreaLayout.fromFields(draggableFieldsInfo.draggable.fields))
  }
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetsHelper.dropTargetsToNodeSide(draggableFieldsInfo)
  def fields(tableDataOption:Option[TableData]) = measureAreaLayout(tableDataOption).allFields
  def nodes = List(new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, this, fieldBindings,
    tableStateGeneratorProperty))
  private def measureAreaLayout(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.measureAreaLayout
  }
  private def measureAreaLayout:MeasureAreaLayout = measureAreaLayout(None)
  private def parentMeasureAreaLayoutNode = mainContent.getChildren.get(0).asInstanceOf[MeasureAreaLayoutNode]
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val newMeasureAreaLayout = parentMeasureAreaLayoutNode.generateMeasureAreaLayoutWithRemoval(draggableFieldsInfo.draggable)
    tableData.withMeasureAreaLayout(newMeasureAreaLayout)
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