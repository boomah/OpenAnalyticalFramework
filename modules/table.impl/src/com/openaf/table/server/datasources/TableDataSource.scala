package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{TableData, TableState}

trait TableDataSource {
  def tableData(tableState:TableState):TableData
}
