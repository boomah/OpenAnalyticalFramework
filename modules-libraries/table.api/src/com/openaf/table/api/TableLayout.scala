package com.openaf.table.api

case class TableLayout(rowFields:List[Field], measureAreaLayout:MeasureAreaLayout, filterFields:List[FieldWithSelection])
object TableLayout {
  val Blank = TableLayout(Nil, MeasureAreaLayout.Blank, Nil)
}
