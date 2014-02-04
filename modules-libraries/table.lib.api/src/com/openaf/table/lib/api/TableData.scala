package com.openaf.table.lib.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState, tableValues:TableValues,
                     defaultRenderers:Map[Field[_],Renderer[_]]) {
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def withRowHeaderFields(newRowHeaderFields:List[Field[_]]) = {
    withTableState(tableState.withRowHeaderFields(newRowHeaderFields))
  }
  def withFilterFields(newFilterFields:List[Field[_]]) = withTableState(tableState.withFilterFields(newFilterFields))
  def withMeasureAreaLayout(newMeasureAreaLayout:MeasureAreaLayout) = {
    withTableState(tableState.withMeasureAreaLayout(newMeasureAreaLayout))
  }
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank, TableValues.Empty, Map.empty)
}

case object NoValue

case class TableValues(rowHeaders:Array[Array[Int]], columnHeaders:Array[Array[Array[Int]]],
                       data:Array[Array[Array[Any]]], valueLookUp:Map[FieldID,Array[Any]])

object TableValues {
  val Empty = TableValues(Array.empty, Array.empty, Array.empty, Map.empty)
}