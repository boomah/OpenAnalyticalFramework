package com.openaf.table.gui

import com.openaf.table.api.{Field, TableData}
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}

class RowHeaderFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends FlatDragAndDropNode {
  getStyleClass.add("row-header-fields-area")
  def description = new SimpleStringProperty("Drop Row Header Fields Here")
  def fields(tableDataOption:Option[TableData]) = tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.rowHeaderFields
  def withNewFields(fields:List[Field], tableData:TableData) = tableData.withRowHeaderFields(fields)
}
