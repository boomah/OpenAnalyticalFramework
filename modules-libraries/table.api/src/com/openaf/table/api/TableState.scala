package com.openaf.table.api

case class TableState(tableLayout:TableLayout)
object TableState {
  val Blank = TableState(TableLayout.Blank)
}
