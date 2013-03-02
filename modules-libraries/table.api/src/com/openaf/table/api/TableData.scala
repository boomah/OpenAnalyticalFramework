package com.openaf.table.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState) {
  def fields = fieldGroup.fields
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = withTableState(tableState.withRowHeaderFields(newRowHeaderFields))
  def withFilterFields(newFilterFields:List[Field]) = withTableState(tableState.withFilterFields(newFilterFields))
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank)
}