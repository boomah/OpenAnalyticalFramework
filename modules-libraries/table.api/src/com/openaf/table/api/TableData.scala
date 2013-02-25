package com.openaf.table.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState) {
  def fields = fieldGroup.fields
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank)
}