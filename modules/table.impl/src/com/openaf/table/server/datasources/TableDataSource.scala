package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{TableData, TableState}
import com.openaf.table.server.FieldDefinitionGroups

trait TableDataSource {
  def fieldDefinitionGroups:FieldDefinitionGroups
  def tableData(tableState:TableState):TableData
  def defaultTableState = TableState.Blank
}
