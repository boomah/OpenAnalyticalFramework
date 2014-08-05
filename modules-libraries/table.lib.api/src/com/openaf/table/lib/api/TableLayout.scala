package com.openaf.table.lib.api

case class TableLayout(rowHeaderFields:List[Field[_]], columnHeaderLayout:ColumnHeaderLayout,
                       filterFields:List[Field[_]]) {
  {
    // All fields must have unique keys
    val allKeys = rowHeaderFields.map(_.key).toSet ++ columnHeaderLayout.allFields.map(_.key).toSet ++ filterFields.map(_.key).toSet
    val numFields = rowHeaderFields.size + columnHeaderLayout.allFields.size + filterFields.size
    require(allKeys.size == numFields, "Fields in TableLayout must all have a unique key")
  }

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
  def remove(fields:List[Field[_]]) = {
    val fieldsSet = fields.toSet
    TableLayout(
      rowHeaderFields.filterNot(fieldsSet.contains),
      columnHeaderLayout.remove(fields:_*),
      filterFields.filterNot(fieldsSet.contains)
    )
  }
  def remove(field:Field[_]):TableLayout = remove(field::Nil)
}

object TableLayout {
  val Blank = TableLayout(Nil, ColumnHeaderLayout.Blank, Nil)
}
