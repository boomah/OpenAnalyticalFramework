package com.openaf.table.lib.api

case class TableState(tableLayout:TableLayout) {
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = copy(tableLayout = tableLayout.withRowHeaderFields(newRowHeaderFields))
  def withFilterFields(newFilterFields:List[Field]) = copy(tableLayout = tableLayout.withFilterFields(newFilterFields))
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = copy(tableLayout = tableLayout.withMeasureAreaLayout(newMeasureAreaLayout))
  def allFields = tableLayout.allFields
}
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
