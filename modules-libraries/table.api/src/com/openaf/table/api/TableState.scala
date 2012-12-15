package com.openaf.table.api

case class TableState(tableLayout:TableLayout) {
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = copy(tableLayout = tableLayout.withRowHeaderFields(newRowHeaderFields))
}
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
