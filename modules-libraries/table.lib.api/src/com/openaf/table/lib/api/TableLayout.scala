package com.openaf.table.lib.api

case class TableLayout(rowHeaderFields:List[Field], measureAreaLayout:MeasureAreaLayout, filterFields:List[Field],
                       filters:Map[Field,Selection]) {
  require(
    filters.keySet == allFields,
    "There should be a filter for every field in the layout and no filters for fields not in the layout"
  )
  def allFields = rowHeaderFields.toSet ++ measureAreaLayout.allFields.toSet ++ filterFields.toSet
  private def newFilters(fields:List[Field], newAllFields:Set[Field]) = {
    (filters ++ fields.map(field => {
      field -> filters.getOrElse(field, AllSelection)
    })).filterKeys(newAllFields.contains).map(identity)
  }
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = {
    val newAllFields = newRowHeaderFields.toSet ++ measureAreaLayout.allFields.toSet ++ filterFields.toSet
    copy(
      rowHeaderFields = newRowHeaderFields,
      filters = newFilters(newRowHeaderFields, newAllFields)
    )
  }
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    val normalisedNewMeasureAreaLayout = newMeasureAreaLayout.normalise
    val newMeasureAreaLayoutFields = normalisedNewMeasureAreaLayout.allFields
    val newAllFields = rowHeaderFields.toSet ++ newMeasureAreaLayoutFields.toSet ++ filterFields.toSet
    copy(
      measureAreaLayout = normalisedNewMeasureAreaLayout,
      filters = newFilters(newMeasureAreaLayoutFields, newAllFields)
    )
  }
  def withFilterFields(newFilterFields:List[Field]) = {
    val newAllFields = rowHeaderFields.toSet ++ measureAreaLayout.allFields.toSet ++ newFilterFields.toSet
    copy(
      filterFields = newFilterFields,
      filters = newFilters(newFilterFields, newAllFields)
    )
  }
}

object TableLayout {
  val Blank = TableLayout(Nil, MeasureAreaLayout.Blank, Nil, Map.empty)
}
