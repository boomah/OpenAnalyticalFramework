package com.openaf.table.gui

import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.lib.api.{FieldID, TableData, ColumnHeaderLayout}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class ColumnHeaderArea(val tableDataProperty:Property[TableData], val dragAndDrop:DragAndDrop,
                        val locale:Property[Locale],
                        fieldBindings:ObservableMap[FieldID,StringBinding]) extends DragAndDropContainerNode {
  getStyleClass.add("column-header-area")
  private val dropTargetsHelper = new ColumnHeaderAreaDropTargetsHelper(mainContent, dropTargetPane, this)

  def descriptionID = "columnHeaderDescription"
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetsHelper.dropTargetsToNodeSide(draggableFieldsInfo)
  private def parentColumnHeaderLayoutNode = mainContent.getChildren.get(0).asInstanceOf[ColumnHeaderLayoutNode]
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val newColumnHeaderLayout = parentColumnHeaderLayoutNode.generateColumnHeaderLayoutWithRemoval(draggableFieldsInfo.draggable)
    tableData.withColumnHeaderLayout(newColumnHeaderLayout)
  }
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val newColumnHeaderLayout = if (tableData.tableState.tableLayout.columnHeaderLayout.allFields.isEmpty) {
      ColumnHeaderLayout.fromFields(draggableFieldsInfo.draggable.fields)
    } else {
      val nodeSide = dropTargetMap(dropTarget)
      parentColumnHeaderLayoutNode.generateColumnHeaderLayoutWithAddition(nodeSide, draggableFieldsInfo)
    }
    tableData.withColumnHeaderLayout(newColumnHeaderLayout)
  }

  private def fullSetup(columnHeaderLayout:ColumnHeaderLayout) {
    val node = new ColumnHeaderLayoutNode(columnHeaderLayout, tableDataProperty, dragAndDrop, this, fieldBindings)
    mainContent.getChildren.clear()
    mainContent.getChildren.add(node)
  }

  def setup(oldTableDataOption:Option[TableData], newTableData:TableData) {
    val newColumnHeaderLayout = newTableData.tableState.tableLayout.columnHeaderLayout
    if (newColumnHeaderLayout.allFields.isEmpty) {
      setupForEmpty()
    } else {
      oldTableDataOption match {
        case None => fullSetup(newColumnHeaderLayout)
        case Some(oldTableData) => {
          if (oldTableData.tableState.tableLayout.columnHeaderLayout != newColumnHeaderLayout) {
            fullSetup(newColumnHeaderLayout)
          }
        }
      }
    }
  }
}