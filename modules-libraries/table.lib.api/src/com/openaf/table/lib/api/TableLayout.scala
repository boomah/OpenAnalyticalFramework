package com.openaf.table.lib.api

case class TableLayout(rowHeaderFields:List[Field[_]], columnHeaderLayout:ColumnHeaderLayout,
                       filterFields:List[Field[_]]) {
  def rowHeaderFieldIDs = rowHeaderFields.map(_.id)
  def columnHeaderLayoutFieldIDs = columnHeaderLayout.allFields.map(_.id)
  def filterFieldIDs = filterFields.map(_.id)
  def allFields = rowHeaderFields.toSet ++ columnHeaderLayout.allFields.toSet ++ filterFields.toSet
  def distinctFieldIDs = (rowHeaderFieldIDs ::: columnHeaderLayoutFieldIDs ::: filterFieldIDs).distinct
  def withRowHeaderFields(newRowHeaderFields:List[Field[_]]) = copy(rowHeaderFields = newRowHeaderFields)
  def withColumnHeaderLayout(newColumnHeaderLayout:ColumnHeaderLayout) = {
    copy(columnHeaderLayout = newColumnHeaderLayout.normalise)
  }
  def withFilterFields(newFilterFields:List[Field[_]]) = copy(filterFields = newFilterFields)
  def remove(fields:Field[_]*) = {
    copy(
      rowHeaderFields = rowHeaderFields.filterNot(fields.contains),
      columnHeaderLayout = columnHeaderLayout.remove(fields:_*),
      filterFields = filterFields.filterNot(fields.contains)
    )
  }
}

object TableLayout {
  val Blank = TableLayout(Nil, ColumnHeaderLayout.Blank, Nil)
}
