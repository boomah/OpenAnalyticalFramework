package com.openaf.sport

import com.openaf.table.server._
import com.openaf.sport.api.SportPage._
import com.openaf.table.lib.api.FieldID
import com.openaf.table.server.datasources.UnfilteredArrayTableDataSource

class GoalsTableDataSource extends UnfilteredArrayTableDataSource {
  val fieldDefinitions:List[Either[FieldDefinitionGroup,FieldDefinition]] = List(
    StringFieldDefinition(PlayerField), IntFieldDefinition(TimeField), StringFieldDefinition(TeamField),
    StringFieldDefinition(OppositionTeamField), StringFieldDefinition(VenueField), StringFieldDefinition(DateField),
    StringFieldDefinition(KickOffTimeField), StringFieldDefinition(CompetitionField)
  ).map(fieldDefinition => Right(fieldDefinition))

  val data:Array[Array[Any]] = Array(
    Array("Ba", 54, "Newcastle United", "Tottenham Hotspur", "Sports Direct Arena", "18Aug12", "17:30", "Barclays Premier League"),
    Array("Defoe", 76, "Tottenham Hotspur", "Newcastle United", "Sports Direct Arena", "18Aug12", "17:30", "Barclays Premier League"),
    Array("Ben Arfa", 80, "Newcastle United", "Tottenham Hotspur", "Sports Direct Arena", "18Aug12", "17:30", "Barclays Premier League"),
    Array("Nolan", 40, "West Ham United", "Aston Villa", "Upton Park", "18Aug12", "15:00", "Barclays Premier League"),
    Array("Tevez", 40, "Manchester City", "Southampton", "Etihad Stadium", "19Aug12", "16:00", "Barclays Premier League"),
    Array("Lambert", 59, "Southampton", "Manchester City", "Etihad Stadium", "19Aug12", "16:00", "Barclays Premier League"),
    Array("S Davis", 68, "Southampton", "Manchester City", "Etihad Stadium", "19Aug12", "16:00", "Barclays Premier League"),
    Array("Dzeko", 72, "Manchester City", "Southampton", "Etihad Stadium", "19Aug12", "16:00", "Barclays Premier League"),
    Array("Nasri", 80, "Manchester City", "Southampton", "Etihad Stadium", "19Aug12", "16:00", "Barclays Premier League"),
    Array("Ivanovic", 2, "Chelsea", "Wigan Athletic", "The DW Stadium", "19Aug12", "13:30", "Barclays Premier League"),
    Array("Lampard", 7, "Chelsea", "Wigan Athletic", "The DW Stadium", "19Aug12", "13:30", "Barclays Premier League")
  )
  val fieldIDs:Array[FieldID] = Array(PlayerField.id, TimeField.id, TeamField.id, OppositionTeamField.id, VenueField.id,
    DateField.id, KickOffTimeField.id, CompetitionField.id)

  def fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard, FieldDefinitionGroup("Goals", fieldDefinitions)))
}
