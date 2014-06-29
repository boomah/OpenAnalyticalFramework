package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{TableStateGenerator, FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class FilterFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                       val locale:SimpleObjectProperty[Locale],
                       val fieldBindings:ObservableMap[FieldID,StringBinding],
                       val tableStateGenerator:SimpleObjectProperty[TableStateGenerator]) extends FlatDragAndDropContainerNode {
  getStyleClass.add("filter-fields-area")
  def descriptionID = "filterDescription"
  def fields(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.filterFields
  }
  def withNewFields(fields:List[Field[_]], tableData:TableData) = tableData.withFilterFields(fields)
}
