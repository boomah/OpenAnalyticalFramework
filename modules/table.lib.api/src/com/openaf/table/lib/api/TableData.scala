package com.openaf.table.lib.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState, tableValues:TableValues) {
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def rowHeaderFields = tableState.rowHeaderFields
  def columnHeaderLayout = tableState.columnHeaderLayout
  def replaceField(oldField:Field[_], newField:Field[_]) = withTableState(tableState.replaceField(oldField, newField))
  def generateFieldKeys = withTableState(tableState.generateFieldKeys)
  def numRows = tableValues.numRows
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank, TableValues.Empty)
}

case object NoValue

class OpenAFTableRow(val row:Int, val rowHeaderValues:Array[Int], val columnHeaderAndDataValues:Array[Any]) extends Serializable {
  def numColumnHeaderColumns = columnHeaderAndDataValues.length
  override def toString = s"${getClass.getSimpleName}($row,${rowHeaderValues.toList},${columnHeaderAndDataValues.toList})"
}

case class TableValues(rows:Array[OpenAFTableRow], fieldPathsIndexes:Array[Int], fieldValues:FieldValues,
                       valueLookUp:Map[FieldID,Array[Any]]) {
  def numRows = rows.length
}

object TableValues {
  val Empty = TableValues(Array.empty, Array.empty, FieldValues.Empty, Map.empty)

  val FieldInt = 0
  val NoValueInt = -1
  val TotalTopInt = -2
  val TotalBottomInt = -3
}

case class FieldValues(values:Map[Field[_],Array[Int]])

object FieldValues {
  val Empty = FieldValues(Map.empty)
}