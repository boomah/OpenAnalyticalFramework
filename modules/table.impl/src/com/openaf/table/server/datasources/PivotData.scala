package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldID, FieldValues, TableState}
import com.openaf.table.server.FieldDefinitionGroups

case class PivotData(tableState:TableState, fieldDefinitionGroups:FieldDefinitionGroups, rowHeaderValues:Array[Array[Int]],
                     columnHeaderPaths:Array[ColumnHeaderPath], data:Map[DataPath,Any], fieldValues:FieldValues,
                     valueLookUp:Map[FieldID,Array[Any]]) {
  assert(tableState.generateFieldKeys == tableState, "TableState should have keys by this point")
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

object PivotData {
  val Empty = PivotData(TableState.Blank, FieldDefinitionGroups.Empty, Array.empty, Array.empty, Map.empty,
    FieldValues.Empty, Map.empty)
}

