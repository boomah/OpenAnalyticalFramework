package com.openaf.table.lib.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState, rowHeaders:Array[Array[Int]],
                     columnHeaders:Array[Array[Array[Int]]], data:Array[Array[Array[Any]]],
                     valueLookUp:Map[String,Array[Any]]) {
  def fields = fieldGroup.fields
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def withRowHeaderFields(newRowHeaderFields:List[Field]) = withTableState(tableState.withRowHeaderFields(newRowHeaderFields))
  def withFilterFields(newFilterFields:List[Field]) = withTableState(tableState.withFilterFields(newFilterFields))
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = withTableState(tableState.withMeasureAreaLayout(newMeasureAreaLayout))
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank, Array.empty, Array.empty, Array.empty, Map.empty)
}

case object NoValue