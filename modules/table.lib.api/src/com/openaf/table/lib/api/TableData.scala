package com.openaf.table.lib.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState, tableValues:TableValues, transformers:Transformers,
                     orderings:Orderings) {
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def withTableValues(newTableValues:TableValues) = copy(tableValues = newTableValues)
  def rowHeaderFields = tableState.rowHeaderFields
  def columnHeaderLayout = tableState.columnHeaderLayout
  def replaceField(oldField:Field[_], newField:Field[_]) = {
    withTableState(tableState.replaceField(oldField, newField))
      .withTableValues(tableValues.replaceField(oldField, newField))
  }
  def generateFieldKeys = withTableState(tableState.generateFieldKeys)
  def numRows = tableValues.numRows
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank, TableValues.Empty, Transformers.Empty, Orderings.Empty)
}

case object NoValue

class OpenAFTableRow(val row:Int, val rowHeaderValues:Array[Int], val columnHeaderAndDataValues:Array[Any]) extends Serializable {
  def numColumnHeaderColumns = columnHeaderAndDataValues.length
  override def toString = s"${getClass.getSimpleName}($row,${rowHeaderValues.toList},${columnHeaderAndDataValues.toList})"
}

case class TableValues(rows:Array[OpenAFTableRow], fieldPathsIndexes:Array[Int], fieldValues:FieldValues,
                       valueLookUp:Map[FieldID,Array[Any]]) {
  def numRows = rows.length
  def replaceField(oldField:Field[_], newField:Field[_]) = copy(fieldValues = fieldValues.replaceField(oldField, newField))
}

object TableValues {
  val Empty = TableValues(Array.empty, Array.empty, FieldValues.Empty, Map.empty)

  val FieldInt = 0
  val NoValueInt = -1
  val TotalTopInt = -2
  val TotalBottomInt = -3
}

case class FieldValues(values:Map[Field[_],Array[Int]]) {
  def replaceField(oldField:Field[_], newField:Field[_]) = {
    val newValues = values.map{case (field,valuesArray) => if (field == oldField) newField -> valuesArray else field -> valuesArray}
    copy(values = newValues.toMap)
  }
}

object FieldValues {
  val Empty = FieldValues(Map.empty)
}

case class Transformers(transformers:Map[FieldID,List[TransformerType[_]]])

object Transformers {
  val Empty = Transformers(Map.empty)
}

case class Orderings(orderings:Map[Field[_],Ordering[_]])

object Orderings {
  val Empty = Orderings(Map.empty)
}