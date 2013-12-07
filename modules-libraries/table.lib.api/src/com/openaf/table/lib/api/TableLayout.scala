package com.openaf.table.lib.api

case class TableLayout(rowHeaderFields:List[Field], measureAreaLayout:MeasureAreaLayout, filterFields:List[Field]) {
  def allFields = rowHeaderFields.toSet ++ measureAreaLayout.allFields.toSet ++ filterFields.toSet
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = copy(rowHeaderFields = newRowHeaderFields)
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    copy(measureAreaLayout = newMeasureAreaLayout.normalise)
  }
  def withFilterFields(newFilterFields:List[Field]) = copy(filterFields = newFilterFields)
}

object TableLayout {
  val Blank = TableLayout(Nil, MeasureAreaLayout.Blank, Nil)
}
