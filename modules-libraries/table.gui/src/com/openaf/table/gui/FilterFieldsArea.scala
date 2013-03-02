package com.openaf.table.gui

import javafx.scene.layout.FlowPane
import javafx.scene.control.Label
import com.openaf.table.api.{AllSelection, FieldWithSelection, TableData}
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ObservableValue, ChangeListener}

class FilterFieldsArea(tableDataProperty:SimpleObjectProperty[TableData], dragAndDrop:DragAndDrop)
  extends FlowPane with DropTargetContainer with DropTarget with DraggableParent {

  private val descriptionLabel = new Label("Drop Filter Fields Here")

  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = {
    if (tableDataProperty.get.tableState.tableLayout.filterFields.isEmpty) {
      List(this)
    } else {
      Nil
    }
  }
  dragAndDrop.register(this)

  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val currentTableState = tableData.tableState
    val newFilterFields = currentTableState.tableLayout.filterFields ++ draggableFieldsInfo.draggable.fields.map(field => FieldWithSelection(field, AllSelection))
    tableData.withTableState(currentTableState.withFilterFields(newFilterFields))
  }

  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData
  }

  private def setup() {
    val filterFields = tableDataProperty.get.tableState.tableLayout.filterFields
    getChildren.clear()
    if (filterFields.isEmpty) {
      getChildren.add(descriptionLabel)
    } else {
      val fieldNodes = filterFields.map(fieldWithSelection => new FieldNode(fieldWithSelection.field, dragAndDrop, this, tableDataProperty))
      getChildren.addAll(fieldNodes.toArray :_*)
    }
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {setup()}
  })

  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val updatedFields = getChildren.toArray.flatMap(child => if (child == draggableFieldsInfo.draggable) None else Some(child))
      .collect{case (draggable:Draggable) => draggable.fields}.flatten.toList.map(FieldWithSelection(_, AllSelection))
    tableData.withTableState(tableData.tableState.withFilterFields(updatedFields))
  }
}
