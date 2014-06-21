package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{TableData, Field}

class FilterFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                       val locale:SimpleObjectProperty[Locale]) extends FlatDragAndDropContainerNode {
  getStyleClass.add("filter-fields-area")
  def descriptionID = "filterDescription"
  def fields(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.filterFields
  }
  def withNewFields(fields:List[Field[_]], tableData:TableData) = tableData.withFilterFields(fields)
}
