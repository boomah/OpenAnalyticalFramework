package com.openaf.table.gui

import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{TableData, Field}

class RowHeaderFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                          val locale:SimpleObjectProperty[Locale]) extends FlatDragAndDropNode {
  getStyleClass.add("row-header-fields-area")
  def descriptionID = "rowHeaderDescription"
  def fields(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.rowHeaderFields
  }
  def withNewFields(fields:List[Field[_]], tableData:TableData) = tableData.withRowHeaderFields(fields)
}
