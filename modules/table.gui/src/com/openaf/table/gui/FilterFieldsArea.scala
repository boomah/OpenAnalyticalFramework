package com.openaf.table.gui

import com.openaf.table.lib.api.{TableState, Field}

class FilterFieldsArea(val tableFields:OpenAFTableFields) extends FlatDragAndDropContainerNode {
  getStyleClass.add("filter-fields-area")
  def descriptionID = "filterDescription"
  def fields(tableStateOption:Option[TableState]) = {
    tableStateOption.getOrElse(tableFields.tableDataProperty.getValue.tableState).filterFields
  }
  def withNewFields(fields:List[Field[_]], tableState:TableState) = tableState.withFilterFields(fields)
}
