package com.openaf.table.gui

import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.lib.api.{TableState, FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding

class RowHeaderFieldsArea(val tableDataProperty:Property[TableData], val requestTableStateProperty:Property[TableState],
                          val dragAndDrop:DragAndDrop, val locale:Property[Locale],
                          val fieldBindings:ObservableMap[FieldID,StringBinding]) extends FlatDragAndDropContainerNode {
  getStyleClass.add("row-header-fields-area")
  def descriptionID = "rowHeaderDescription"
  def fields(tableStateOption:Option[TableState]) = {
    tableStateOption.getOrElse(requestTableStateProperty.getValue).rowHeaderFields
  }
  def withNewFields(fields:List[Field[_]], tableState:TableState) = tableState.withRowHeaderFields(fields)
}
