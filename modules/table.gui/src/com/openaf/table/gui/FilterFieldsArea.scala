package com.openaf.table.gui

import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.lib.api.{TableState, FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class FilterFieldsArea(val tableDataProperty:Property[TableData], val requestTableStateProperty:Property[TableState],
                       val dragAndDrop:DragAndDrop, val locale:Property[Locale],
                       val fieldBindings:ObservableMap[FieldID,StringBinding]) extends FlatDragAndDropContainerNode {
  getStyleClass.add("filter-fields-area")
  def descriptionID = "filterDescription"
  def fields(tableStateOption:Option[TableState]) = {
    tableStateOption.getOrElse(requestTableStateProperty.getValue).filterFields
  }
  def withNewFields(fields:List[Field[_]], tableState:TableState) = tableState.withFilterFields(fields)
}
