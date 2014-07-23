package com.openaf.table.gui

import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.lib.api.{FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class FilterFieldsArea(val tableDataProperty:Property[TableData], val dragAndDrop:DragAndDrop,
                       val locale:Property[Locale],
                       val fieldBindings:ObservableMap[FieldID,StringBinding]) extends FlatDragAndDropContainerNode {
  getStyleClass.add("filter-fields-area")
  def descriptionID = "filterDescription"
  def fields(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.getValue).tableState.tableLayout.filterFields
  }
  def withNewFields(fields:List[Field[_]], tableData:TableData) = tableData.withFilterFields(fields)
}
