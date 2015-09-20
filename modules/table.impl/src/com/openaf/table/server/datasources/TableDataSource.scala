package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{FieldID, TableData, TableState}
import com.openaf.table.server.FieldDefinitionGroups

trait TableDataSource {
  def tableData(tableState:TableState):TableData
}

/**
 * A standard 2D table where the fieldIDs represent the column headings. The width of each element in the data should
 * be the same width as the fieldIDs. The order of the fieldIDs must match the order of the data.
 *
 * There should be FieldDefinitions for the data supplied. The FieldDefinitions should be grouped together in a logical
 * way.
 */
class DataSourceTable(val fieldIDs:Array[FieldID], val data:Array[Array[Any]], val fieldDefinitionGroups:FieldDefinitionGroups) {
  if (data.nonEmpty) {
    require(fieldIDs.length == data(0).length, s"The table has ${fieldIDs.length} columns but the data doesn't match " +
      s"with ${data(0).length} columns")
  }
}

object DataSourceTable {
  def apply(fieldIDs:Array[FieldID], data:Array[Array[Any]], fieldDefinitionGroups:FieldDefinitionGroups) = {
    new DataSourceTable(fieldIDs, data, fieldDefinitionGroups)
  }
}
