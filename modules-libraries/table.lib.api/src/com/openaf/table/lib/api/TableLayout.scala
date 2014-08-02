package com.openaf.table.lib.api

case class TableLayout(rowHeaderFields:List[Field[_]], columnHeaderLayout:ColumnHeaderLayout,
                       filterFields:List[Field[_]]) {
  // All fields need to be unique
  require(rowHeaderFields.size == rowHeaderFields.toSet.size, "Duplicate fields in the rowHeaderFields")
  require(columnHeaderLayout.allFields.size == columnHeaderLayout.allFields.toSet.size, "Duplicate fields in the ColumnHeaderLayout")
  require(filterFields.size == filterFields.toSet.size, "Duplicate fields in the filterFields")
  require(rowHeaderFields.size + columnHeaderLayout.allFields.size + filterFields.size == allFields.size, "There are duplicate fields in the TableLayout")
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
