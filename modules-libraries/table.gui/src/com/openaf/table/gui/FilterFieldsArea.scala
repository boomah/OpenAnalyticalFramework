package com.openaf.table.gui

import com.openaf.table.api.{Field, TableData}
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale

class FilterFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                       val locale:SimpleObjectProperty[Locale]) extends FlatDragAndDropNode {
  getStyleClass.add("filter-fields-area")
  def descriptionID = "filterDescription"
  def fields(tableDataOption:Option[TableData]) = tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.filterFields
  def withNewFields(fields:List[Field], tableData:TableData) = tableData.withFilterFields(fields)
}
