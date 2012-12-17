package com.openaf.table.server

import com.openaf.table.api.{TableData, TableState}

object TableDataGenerator {
  def tableData(tableState:TableState, tableDataSource:TableDataSource) = {
    val fieldGroup = tableDataSource.fieldDefinitionGroup.fieldGroup
    val defaultTableState = tableDataSource.defaultTableState
    val result = tableDataSource.result(tableState)

    TableData(fieldGroup, tableState)
  }
}
