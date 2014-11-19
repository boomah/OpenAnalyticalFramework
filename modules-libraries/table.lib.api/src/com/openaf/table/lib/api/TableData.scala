package com.openaf.table.lib.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState, tableValues:TableValues,
                     defaultRenderers:Map[FieldID,Renderer[_]]) {
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def rowHeaderFields = tableState.rowHeaderFields
  def columnHeaderLayout = tableState.columnHeaderLayout
  def replaceField(oldField:Field[_], newField:Field[_]) = withTableState(tableState.replaceField(oldField, newField))
  def generateFieldKeys = withTableState(tableState.generateFieldKeys)
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank, TableValues.Empty, Map.empty)
}

case object NoValue

class OpenAFTableRow(val row:Int, val rowHeaderValues:Array[Int], val columnHeaderAndDataValues:Array[Any]) extends Serializable {
  override def toString = s"${getClass.getSimpleName}($row,${rowHeaderValues.toList},${columnHeaderAndDataValues.toList})"
}

case class TableValues(rows:Array[OpenAFTableRow], columnsPerPath:Array[Int], fieldValues:FieldValues,
                       valueLookUp:Map[FieldID,Array[Any]])

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