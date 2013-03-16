package com.openaf.table.api

case class TableLayout(rowHeaderFields:List[Field], measureAreaLayout:MeasureAreaLayout, filterFields:List[Field],
                       filters:Map[Field,Selection]) {
  require(filters.keySet == allFields, "There should be a filter for every field in the layout and no filters for fields not in the layout")
  def allFields = rowHeaderFields.toSet ++ measureAreaLayout.allFields ++ filterFields.toSet
  private def newFilters(fields:List[Field], newAllFields:Set[Field]) = {
    (filters ++ fields.map(field => field -> filters.getOrElse(field, AllSelection))).filterKeys(newAllFields.contains).map(identity)
  }
  private def newFilters(fields:Set[Field], newAllFields:Set[Field]):Map[Field,Selection] = {newFilters(fields.toList, newAllFields)}
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = {
    val newAllFields = newRowHeaderFields.toSet ++ measureAreaLayout.allFields ++ filterFields.toSet
    copy(rowHeaderFields = newRowHeaderFields, filters = newFilters(newRowHeaderFields, newAllFields))
  }
  def withFilterFields(newFilterFields:List[Field]) = {
    val newAllFields = rowHeaderFields.toSet ++ measureAreaLayout.allFields ++ newFilterFields.toSet
    copy(filterFields = newFilterFields, filters = newFilters(newFilterFields, newAllFields))
  }
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    val newMeasureAreaLayoutFields = newMeasureAreaLayout.allFields
    val newAllFields = rowHeaderFields.toSet ++ newMeasureAreaLayoutFields ++ filterFields.toSet
    copy(measureAreaLayout = newMeasureAreaLayout, filters = newFilters(newMeasureAreaLayoutFields, newAllFields))
  }
}
object TableLayout {
  val Blank = TableLayout(Nil, MeasureAreaLayout.Blank, Nil, Map.empty)
}
