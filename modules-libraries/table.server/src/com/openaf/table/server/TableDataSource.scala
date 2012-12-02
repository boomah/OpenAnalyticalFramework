package com.openaf.table.server

import com.openaf.table.api.{Field, TableState}

trait TableDataSource {
  def fieldDefinitionGroups:FieldDefinitionGroups
  def result(tableState:TableState):Result
  def defaultTableState = TableState.Blank
}

case class ResultRow(fieldLookUp:Map[Field,Int], values:Array[Any])
case class Result(resultRows:Array[ResultRow])
object Result {
  val Empty = Result(Array.empty)
}