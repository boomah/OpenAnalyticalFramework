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
  def remove(fields:List[Field[_]]) = {
    checkFieldsAreUnique()
    val fieldsSet = fields.toSet
    withRowHeaderFields(rowHeaderFields.filterNot(fieldsSet.contains))
      .withColumnHeaderLayout(columnHeaderLayout.remove(fields:_*))
      .withFilterFields(filterFields.filterNot(fieldsSet.contains))
  }
  def remove(field:Field[_]):TableLayout = remove(field::Nil)
  def replaceField(oldField:Field[_], newField:Field[_]) = {
    checkFieldsAreUnique()
    val newFilterFields = filterFields.map(field => {if (field == oldField) newField else field})
    val newRowHeaderFields = rowHeaderFields.map(field => {if (field == oldField) newField else field})
    val newColumnHeaderLayout = columnHeaderLayout.replaceField(oldField, newField)
    withFilterFields(newFilterFields).withRowHeaderFields(newRowHeaderFields).withColumnHeaderLayout(newColumnHeaderLayout)
  }
  private def checkFieldsAreUnique() {
    // Certain operations on table layout (such as remove and replace) require the fields to be unique.
    val numFields = rowHeaderFields.size + columnHeaderLayout.allFields.size + filterFields.size
    require(allFields.size == numFields, "Fields in TableLayout must all be unique")
  }
  def generateFieldKeys = {
    val newRowHeaderFields = rowHeaderFields.zipWithIndex.map{case (field,number) => field.withKey(RowHeaderFieldKey(number))}
    val newColumnHeaderLayout = columnHeaderLayout.generateFieldKeys
    val newFilterFields = filterFields.zipWithIndex.map{case (field,number) => field.withKey(FilterFieldKey(number))}
    withRowHeaderFields(newRowHeaderFields).withColumnHeaderLayout(newColumnHeaderLayout).withFilterFields(newFilterFields)
  }
  def isColumnHeaderField(field:Field[_]) = columnHeaderLayout.allFields.contains(field)
  def withDefaultRendererIds = {
    val newRowHeaderFields = rowHeaderFields.map(_.withDefaultRendererId)
    val newColumnHeaderLayout = columnHeaderLayout.withDefaultRendererIds
    val newFilterFields = filterFields.map(_.withDefaultRendererId)
    withRowHeaderFields(newRowHeaderFields).withColumnHeaderLayout(newColumnHeaderLayout).withFilterFields(newFilterFields)
  }
}

object TableLayout {
  val Blank = TableLayout(Nil, ColumnHeaderLayout.Blank, Nil)
}
