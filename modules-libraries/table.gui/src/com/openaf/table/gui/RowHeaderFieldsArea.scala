package com.openaf.table.gui

import javafx.scene.layout.FlowPane
import javafx.scene.control.Label
import com.openaf.table.api.TableData
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.{ObservableValue, ChangeListener}

class RowHeaderFieldsArea(tableDataProperty:SimpleObjectProperty[TableData], dragAndDrop:DragAndDrop)
  extends FlowPane with DropTargetContainer with DropTarget with DraggableParent {

  private val descriptionLabel = new Label("Drop Row Header Fields Here")

  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = List(this)
  dragAndDrop.register(this)

  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val currentTableState = tableData.tableState
    val newRowFields = currentTableState.tableLayout.rowHeaderFields ++ draggableFieldsInfo.draggable.fields
    tableData.withTableState(currentTableState.withRowHeaderFields(newRowFields))
  }

  private def setup() {
    val rowHeaderFields = tableDataProperty.get.tableState.tableLayout.rowHeaderFields
    getChildren.clear()
    if (rowHeaderFields.isEmpty) {
      getChildren.add(descriptionLabel)
    } else {
      val fieldNodes = rowHeaderFields.map(field => new FieldNode(field, dragAndDrop, this, tableDataProperty))
      getChildren.addAll(fieldNodes.toArray :_*)
    }
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {setup()}
  })

  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    val updatedFields = getChildren.toArray.flatMap(child => if (child == draggableFieldsInfo.draggable) None else Some(child))
      .collect{case (draggable:Draggable) => draggable.fields}.flatten.toList
    tableData.withTableState(tableData.tableState.withRowHeaderFields(updatedFields))
  }
}
