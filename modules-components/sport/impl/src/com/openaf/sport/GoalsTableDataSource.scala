package com.openaf.sport

import com.openaf.table.server.{Result, FieldDefinitionGroup, FieldDefinition, TableDataSource}
import com.openaf.sport.api.SportPage._
import com.openaf.table.lib.api.TableState

class GoalsTableDataSource extends TableDataSource {
  val fieldDefinitions = List(PlayerNameField).map(field => Right(new FieldDefinition(field)))

  def fieldDefinitionGroup = FieldDefinitionGroup("Fields", fieldDefinitions)
  def result(tableState:TableState) = Result.Empty
}
