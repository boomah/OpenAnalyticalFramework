package com.openaf.table.lib.api

case class TableLayout(rowHeaderFields:List[Field], measureAreaLayout:MeasureAreaLayout, filterFields:List[Field]) {
  def rowHeaderFieldIDs = rowHeaderFields.map(_.id)
  def measureAreaLayoutFieldIDs = measureAreaLayout.allFields.map(_.id)
  def filterFieldIDs = filterFields.map(_.id)
  def allFields = rowHeaderFields.toSet ++ measureAreaLayout.allFields.toSet ++ filterFields.toSet
  def distinctFieldIDs = (rowHeaderFieldIDs ::: measureAreaLayoutFieldIDs ::: filterFieldIDs).distinct
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = copy(rowHeaderFields = newRowHeaderFields)
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    copy(measureAreaLayout = newMeasureAreaLayout.normalise)
  }
  def withFilterFields(newFilterFields:List[Field]) = copy(filterFields = newFilterFields)
}

object TableLayout {
  val Blank = TableLayout(Nil, MeasureAreaLayout.Blank, Nil)
}
