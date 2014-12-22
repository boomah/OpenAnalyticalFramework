package com.openaf.table.gui

import com.openaf.table.lib.api.{TableState, ColumnHeaderLayout}

class ColumnHeaderArea(val tableFields:OpenAFTableFields) extends DragAndDropContainerNode {
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
    val node = new ColumnHeaderLayoutNode(columnHeaderLayout, this, tableFields)
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