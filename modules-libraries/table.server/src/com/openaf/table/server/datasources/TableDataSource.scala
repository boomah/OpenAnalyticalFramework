package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldID, TableState}
import com.openaf.table.server.FieldDefinitionGroups

trait TableDataSource {
  def fieldDefinitionGroups:FieldDefinitionGroups
  def result(tableState:TableState):Result
  def defaultTableState = TableState.Blank
}

case class Result(rowHeaderValues:Array[Array[Int]], pathData:Array[PathData], valueLookUp:Map[FieldID,Array[Any]],
                  resultState:ResultState)

object Result {
  val Empty = Result(Array.empty, Array.empty, Map.empty, ResultState.Default)
}

case class PathData(colHeaderValues:Array[Array[Int]], data:Map[IntArrayWrapperKey,Any])

case class ResultState(filterState:FilterState, sortState:SortState)

object ResultState {
  val Default = ResultState(FilterState.Default, SortState.Default)
}

case class FilterState(isFiltered:Boolean)

object FilterState {
  val Default = FilterState(isFiltered=false)
}

case class SortState(rowHeadersSorted:Boolean, pathDataSorted:Array[Boolean])

object SortState {
  val Default = SortState(rowHeadersSorted=false, Array.empty)
  def allUnsorted(numPaths:Int) = SortState(rowHeadersSorted=false, pathDataSorted=Array.fill(numPaths)(false))
}