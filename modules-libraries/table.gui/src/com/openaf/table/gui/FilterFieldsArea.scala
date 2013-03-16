package com.openaf.table.gui

import com.openaf.table.api.{Field, TableData}
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}

class FilterFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends FlatDragAndDropNode {
  getStyleClass.add("filter-fields-area")
  def description = new SimpleStringProperty("Drop Filter Fields Here")
  def fields(tableDataOption:Option[TableData]) = tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.filterFields
  def withNewFields(fields:List[Field], tableData:TableData) = tableData.withFilterFields(fields)
}
