package com.openaf.sport

import com.openaf.table.server._
import com.openaf.sport.api.SportPage._
import com.openaf.table.lib.api.TableState
import com.openaf.table.server.datasources.{Result, TableDataSource}

class GoalsTableDataSource extends TableDataSource {
  val fieldDefinitions:List[Either[FieldDefinitionGroup,FieldDefinition]] = List(
    PlayerField, TimeField, TeamField, OppositionTeamField, VenueField, DateField, KickOffTimeField, CompetitionField
  ).map(field => Right(DefaultFieldDefinition(field)))

  def fieldDefinitionGroup = FieldDefinitionGroup("Goals", fieldDefinitions)
  def result(tableState:TableState) = Result.Empty
}
