package com.openaf.table.gui

import com.openaf.table.lib.api.{TableState, Field}

class RowHeaderFieldsArea(val tableFields:OpenAFTableFields) extends FlatDragAndDropContainerNode {
  getStyleClass.add("row-header-fields-area")
  def descriptionID = "rowHeaderDescription"
  def fields(tableStateOption:Option[TableState]) = {
    tableStateOption.getOrElse(tableFields.tableDataProperty.getValue.tableState).rowHeaderFields
  }
  def withNewFields(fields:List[Field[_]], tableState:TableState) = tableState.withRowHeaderFields(fields)
}
