package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{FieldID, TableData, MeasureAreaLayout}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                        val locale:SimpleObjectProperty[Locale],
                        fieldBindings:ObservableMap[FieldID,StringBinding]) extends DragAndDropContainerNode {
  getStyleClass.add("measure-fields-area")
  private val dropTargetsHelper = new MeasureFieldsAreaDropTargetsHelper(mainContent, dropTargetPane, this)

  def descriptionID = "measureDescription"
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetsHelper.dropTargetsToNodeSide(draggableFieldsInfo)
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

  private def fullSetup(measureAreaLayout:MeasureAreaLayout) {
    val node = new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, this, fieldBindings)
    mainContent.getChildren.clear()
    mainContent.getChildren.add(node)
  }

  def setup(oldTableDataOption:Option[TableData], newTableData:TableData) {
    val newMeasureAreaLayout = newTableData.tableState.tableLayout.measureAreaLayout
    if (newMeasureAreaLayout.allFields.isEmpty) {
      setupForEmpty()
    } else {
      oldTableDataOption match {
        case None => fullSetup(newMeasureAreaLayout)
        case Some(oldTableData) => {
          if (oldTableData.tableState.tableLayout.measureAreaLayout != newMeasureAreaLayout) {
            fullSetup(newMeasureAreaLayout)
          }
        }
      }
    }
  }
}