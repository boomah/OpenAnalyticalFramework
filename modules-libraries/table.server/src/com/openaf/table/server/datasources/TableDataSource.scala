package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldValues, FieldID, TableState}
import com.openaf.table.server.FieldDefinitionGroups

trait TableDataSource {
  def fieldDefinitionGroups:FieldDefinitionGroups
  def result(tableState:TableState):Result
  def defaultTableState = TableState.Blank
}

case class Result(rowHeaderValues:Array[Array[Int]], columnHeaderPaths:Array[ColumnHeaderPath], data:Map[DataPath,Any],
                  fieldValues:FieldValues, valueLookUp:Map[FieldID,Array[Any]], resultState:ResultState) {
  val numRowHeaderRows = rowHeaderValues.length
  val numRowHeaderColumns = if (rowHeaderValues.nonEmpty) rowHeaderValues(0).length else 0
  val numColumnHeaderRows = {
    var maxLength = 0
    var counter = 0
    while (counter < columnHeaderPaths.length) {
      maxLength = math.max(maxLength, columnHeaderPaths(counter).values.length)
      counter += 1
    }
    maxLength
  }
  val numColumnHeaderColumns = columnHeaderPaths.length
  val numRows = numColumnHeaderRows + numRowHeaderRows
  val numColumns = numRowHeaderColumns + numColumnHeaderColumns
}

object Result {
  val Empty = Result(Array.empty, Array.empty, Map.empty, FieldValues.Empty, Map.empty, ResultState.Default)
}

case class ResultState(filterState:FilterState, totalsState:TotalsState, sortState:SortState)

object ResultState {
  val Default = ResultState(FilterState.Default, TotalsState.Default, SortState.Default)
}

case class FilterState(isFiltered:Boolean)

object FilterState {
  val Default = FilterState(isFiltered = false)
}

case class TotalsState(totalsAdded:Boolean)

object TotalsState {
  val Default = TotalsState(totalsAdded = true)
}

case class SortState(filtersSorted:Boolean, rowHeadersSorted:Boolean, columnHeadersSorted:Boolean)

object SortState {
  val NoSorting = SortState(filtersSorted=false, rowHeadersSorted=false, columnHeadersSorted=false)
  val Default = NoSorting
}