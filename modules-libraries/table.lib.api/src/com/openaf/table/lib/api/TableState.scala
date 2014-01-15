package com.openaf.table.lib.api

case class TableState(tableLayout:TableLayout) {
  def allFields = tableLayout.allFields
  def distinctFieldIDs = tableLayout.distinctFieldIDs
  def withRowHeaderFields(newRowHeaderFields:List[Field[_]]) = {
    copy(tableLayout = tableLayout.withRowHeaderFields(newRowHeaderFields))
  }
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    copy(tableLayout = tableLayout.withMeasureAreaLayout(newMeasureAreaLayout))
  }
  def withFilterFields(newFilterFields:List[Field[_]]) = {
    copy(tableLayout = tableLayout.withFilterFields(newFilterFields))
  }
}
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
