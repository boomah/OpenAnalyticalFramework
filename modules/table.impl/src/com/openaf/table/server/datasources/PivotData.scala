package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldID, FieldValues, TableState}
import com.openaf.table.server.FieldDefinitionGroups

case class PivotData(tableState:TableState, fieldDefinitionGroups:FieldDefinitionGroups, rowHeaderValues:Array[Array[Int]],
                     columnHeaderValues:Array[Array[Int]], aggregator:Aggregator, fieldValues:FieldValues,
                     valueLookUp:Map[FieldID,Array[Any]]) {
  assert(tableState.generateFieldKeys == tableState, "TableState should have keys by this point")
  val numRowHeaderRows = rowHeaderValues.length
  val numRowHeaderColumns = if (rowHeaderValues.nonEmpty) rowHeaderValues(0).length else 0
  val numColumnHeaderRows = {
    var maxLength = 0
    var counter = 0
    while (counter < columnHeaderValues.length) {
      maxLength = math.max(maxLength, columnHeaderValues(counter).length - 1)
      counter += 1
    }
    maxLength
  }
  val numColumnHeaderColumns = columnHeaderValues.length
  val numRows = numColumnHeaderRows + numRowHeaderRows
  val numColumns = numRowHeaderColumns + numColumnHeaderColumns

  def fieldDefinition(id:FieldID) = fieldDefinitionGroups.fieldDefinition(id)
}

object PivotData {
  val Empty = PivotData(TableState.Blank, FieldDefinitionGroups.Empty, Array.empty, Array.empty, Aggregator.Empty,
    FieldValues.Empty, Map.empty)
}

