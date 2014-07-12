package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldID, TableState}
import com.openaf.table.server.FieldDefinitionGroups

trait TableDataSource {
  def fieldDefinitionGroups:FieldDefinitionGroups
  def result(tableState:TableState):Result
  def defaultTableState = TableState.Blank
}

case class Result(rowHeaderValues:Array[Array[Int]], pathData:Array[PathData], valueLookUp:Map[FieldID,Array[Any]],
                  resultDetails:ResultDetails)

object Result {
  val Empty = Result(Array.empty, Array.empty, Map.empty, ResultDetails.Default)
}

case class PathData(colHeaderValues:Array[Array[Int]], data:Map[IntArrayWrapperKey,Any])

case class ResultDetails(sortDetails:SortDetails)

object ResultDetails {
  val Default = ResultDetails(SortDetails.Default)
}

case class SortDetails(rowHeadersSorted:Boolean, pathDataSorted:Array[Boolean])

object SortDetails {
  val Default = SortDetails(rowHeadersSorted=false, Array.empty)
  def allUnsorted(numPaths:Int) = SortDetails(rowHeadersSorted=false, pathDataSorted=Array.fill(numPaths)(false))
}