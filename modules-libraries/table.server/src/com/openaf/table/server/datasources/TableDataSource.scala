package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{TableState, Field}
import com.openaf.table.server.FieldDefinitionGroup

trait TableDataSource {
  def fieldDefinitionGroup:FieldDefinitionGroup
  def result(tableState:TableState):Result
  def defaultTableState = TableState.Blank
}

case class Result(rowHeaderValues:Array[Array[Int]], pathData:Array[PathData], valueLookUp:Map[String,Array[Any]])

object Result {
  val Empty = Result(Array.empty, Array.empty, Map.empty)
}

case class PathData(colHeaderValues:Array[Array[Int]], data:Map[(List[Int],List[Int]),Any])

