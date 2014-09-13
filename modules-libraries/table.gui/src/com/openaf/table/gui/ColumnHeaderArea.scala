package com.openaf.table.gui

import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.lib.api.{TableState, FieldID, TableData, ColumnHeaderLayout}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class ColumnHeaderArea(tableDataProperty:Property[TableData], val requestTableStateProperty:Property[TableState],
                       val dragAndDrop:DragAndDrop, val locale:Property[Locale],
                       fieldBindings:ObservableMap[FieldID,StringBinding]) extends DragAndDropContainerNode {
  getStyleClass.add("column-header-area")
  private val dropTargetsHelper = new ColumnHeaderAreaDropTargetsHelper(mainContent, dropTargetPane, this)

  def descriptionID = "columnHeaderDescription"
  def dropTargetsToNodeSide(draggableFieldsInfo:DraggableFieldsInfo) = dropTargetsHelper.dropTargetsToNodeSide(draggableFieldsInfo)
  private def parentColumnHeaderLayoutNode = mainContent.getChildren.get(0).asInstanceOf[ColumnHeaderLayoutNode]
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableState:TableState) = {
    val newColumnHeaderLayout = if (tableState.tableLayout.columnHeaderLayout.allFields.isEmpty) {
      ColumnHeaderLayout.fromFields(draggableFieldsInfo.fields)
    } else {
      val nodeSide = dropTargetMap(dropTarget)
      parentColumnHeaderLayoutNode.generateColumnHeaderLayoutWithAddition(nodeSide, draggableFieldsInfo)
    }
    tableState.withColumnHeaderLayout(newColumnHeaderLayout)
  }

  private def fullSetup(columnHeaderLayout:ColumnHeaderLayout) {
    val node = new ColumnHeaderLayoutNode(columnHeaderLayout, tableDataProperty, requestTableStateProperty, dragAndDrop,
      this, fieldBindings, locale)
    mainContent.getChildren.clear()
    mainContent.getChildren.add(node)
  }

  def setup(oldTableStateOption:Option[TableState], newTableState:TableState) {
    val newColumnHeaderLayout = newTableState.tableLayout.columnHeaderLayout
    if (newColumnHeaderLayout.allFields.isEmpty) {
      setupForEmpty()
    } else {
      oldTableStateOption match {
        case None => fullSetup(newColumnHeaderLayout)
        case Some(oldTableState) => {
          if (oldTableState.tableLayout.columnHeaderLayout != newColumnHeaderLayout) {
            fullSetup(newColumnHeaderLayout)
          }
        }
      }
    }
  }
}