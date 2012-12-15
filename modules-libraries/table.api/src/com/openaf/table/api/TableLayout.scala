package com.openaf.table.api

case class TableLayout(rowHeaderFields:List[Field], measureAreaLayout:MeasureAreaLayout, filterFields:List[FieldWithSelection]) {
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = copy(rowHeaderFields = newRowHeaderFields)
}
object TableLayout {
  val Blank = TableLayout(Nil, MeasureAreaLayout.Blank, Nil)
}
