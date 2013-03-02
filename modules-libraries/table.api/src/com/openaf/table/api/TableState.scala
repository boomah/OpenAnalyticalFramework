package com.openaf.table.api

case class TableState(tableLayout:TableLayout) {
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = copy(tableLayout = tableLayout.withRowHeaderFields(newRowHeaderFields))
  def withFilterFields(newFilterFields:List[Field]) = copy(tableLayout = tableLayout.withFilterFields(newFilterFields))
  def allFields = tableLayout.allFields
}
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
