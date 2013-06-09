package com.openaf.table.server

import com.openaf.table.lib.api.{TableState, Field}

trait TableDataSource {
  def fieldDefinitionGroup:FieldDefinitionGroup
  def result(tableState:TableState):Result
  def defaultTableState = TableState.Blank
}

case class ResultRow(fieldLookUp:Map[Field,Int], values:Array[Any])
case class Result(resultRows:Array[ResultRow])
object Result {
  val Empty = Result(Array.empty)
}