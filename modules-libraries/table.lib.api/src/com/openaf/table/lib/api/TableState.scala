package com.openaf.table.lib.api

case class TableState(tableLayout:TableLayout) {
  def allFields = tableLayout.allFields
  def distinctFieldIDs = tableLayout.distinctFieldIDs
  def rowHeaderFields = tableLayout.rowHeaderFields
  def withRowHeaderFields(newRowHeaderFields:List[Field[_]]) = {
    copy(tableLayout = tableLayout.withRowHeaderFields(newRowHeaderFields))
  }
  def measureAreaLayout = tableLayout.measureAreaLayout
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    copy(tableLayout = tableLayout.withMeasureAreaLayout(newMeasureAreaLayout))
  }
  def withFilterFields(newFilterFields:List[Field[_]]) = {
    copy(tableLayout = tableLayout.withFilterFields(newFilterFields))
  }
  def remove(fields:Field[_]*) = copy(tableLayout = tableLayout.remove(fields:_*))
}
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
