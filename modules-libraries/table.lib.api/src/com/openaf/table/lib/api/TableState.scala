package com.openaf.table.lib.api

case class TableState(tableLayout:TableLayout) {
  def allFields = tableLayout.allFields
  def distinctFieldIDs = tableLayout.distinctFieldIDs
  def rowHeaderFields = tableLayout.rowHeaderFields
  def withRowHeaderFields(newRowHeaderFields:List[Field[_]]) = {
    copy(tableLayout = tableLayout.withRowHeaderFields(newRowHeaderFields))
  }
  def columnHeaderLayout = tableLayout.columnHeaderLayout
  def withColumnHeaderLayout(newColumnHeaderLayout:ColumnHeaderLayout) = {
    copy(tableLayout = tableLayout.withColumnHeaderLayout(newColumnHeaderLayout))
  }
  def withFilterFields(newFilterFields:List[Field[_]]) = {
    copy(tableLayout = tableLayout.withFilterFields(newFilterFields))
  }
  def remove(fields:List[Field[_]]) = copy(tableLayout = tableLayout.remove(fields))
}
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
