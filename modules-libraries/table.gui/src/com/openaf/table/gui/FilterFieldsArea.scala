package com.openaf.table.gui

import javafx.scene.layout.FlowPane
import javafx.scene.control.Label
import com.openaf.table.api.{AllSelection, FieldWithSelection, TableData}
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ObservableValue, ChangeListener}

class FilterFieldsArea(tableDataProperty:SimpleObjectProperty[TableData], dragAndDrop:DragAndDrop) extends FlowPane with DropTargetContainer with DropTarget with DraggableParent {
  private val descriptionLabel = new Label("Drop Filter Fields Here")

  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = List(this)
  dragAndDrop.register(this)

  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val currentTableState = tableData.tableState
    val newFilterFields = currentTableState.tableLayout.filterFields ++ draggableFieldsInfo.fields.map(field => FieldWithSelection(field, AllSelection))
    tableData.withTableState(currentTableState.withFilterFields(newFilterFields))
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
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {
      println("Filter area - setting up table state")
      setup()
    }
  })

  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val updatedFields = tableData.tableState.tableLayout.filterFields.filterNot(fieldWithSelection => draggableFieldsInfo.fields.contains(fieldWithSelection.field))
    tableData.withTableState(tableData.tableState.withFilterFields(updatedFields))
  }
}
