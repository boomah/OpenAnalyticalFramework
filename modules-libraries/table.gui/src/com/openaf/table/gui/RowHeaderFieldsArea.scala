package com.openaf.table.gui

import com.openaf.table.api.{Field, TableData}
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale

class RowHeaderFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop,
                          val locale:SimpleObjectProperty[Locale]) extends FlatDragAndDropNode {
  getStyleClass.add("row-header-fields-area")
  def descriptionID = "rowHeaderDescription"
  def fields(tableDataOption:Option[TableData]) = tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.rowHeaderFields
  def withNewFields(fields:List[Field], tableData:TableData) = tableData.withRowHeaderFields(fields)
}
