package com.openaf.sport

import com.openaf.table.server._
import com.openaf.sport.api.SportPage._
import com.openaf.table.lib.api.{FieldID, TableState}
import com.openaf.table.server.datasources.{RawRowBasedTableDataSource, TableDataSource}

class GoalsTableDataSource extends TableDataSource {
  val fieldDefinitions:List[Either[FieldDefinitionGroup,FieldDefinition]] = List(
    StringFieldDefinition(PlayerField), IntFieldDefinition(TimeField), StringFieldDefinition(TeamField),
    StringFieldDefinition(OppositionTeamField), StringFieldDefinition(VenueField), StringFieldDefinition(DateField),
    StringFieldDefinition(KickOffTimeField), StringFieldDefinition(CompetitionField)
  ).map(fieldDefinition => Right(fieldDefinition))

  val data:Array[Array[Any]] = Array(
    Array("Ba", 54, "Newcastle United", "Tottenham Hotspur", "Sports Direct Arena", "18Aug12", "17:30", "Barclays Premier League"),
    Array("Defoe", 76, "Tottenham Hotspur", "Newcastle United", "Sports Direct Arena", "18Aug12", "17:30", "Barclays Premier League")
  )
  val fieldIDs:Array[FieldID] = Array(PlayerField.id, TimeField.id, TeamField.id, OppositionTeamField.id, VenueField.id,
    DateField.id, KickOffTimeField.id, CompetitionField.id)

  def fieldDefinitionGroup = FieldDefinitionGroup("Goals", fieldDefinitions)
  def result(tableState:TableState) = RawRowBasedTableDataSource.result(tableState, data, fieldIDs, fieldDefinitionGroup)
}
