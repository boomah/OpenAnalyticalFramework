package com.openaf.travel

import com.openaf.table.server.{FieldDefinitionGroups, Result, TableDataSource}
import com.openaf.table.api.TableState

class HotelsTableDataSource extends TableDataSource {
  def fieldDefinitionGroups = FieldDefinitionGroups.Empty
  def result(tableState:TableState) = Result.Empty
}
