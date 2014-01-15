package com.openaf.sport.api

import com.openaf.table.api.{TablePageData, TablePage}
import com.openaf.pagemanager.api.{PageFactory, ServerContext}
import com.openaf.table.lib.api.{Field, TableData, TableState}

trait SportPage extends TablePage {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[SportPageDataFacility])
}

object SportPage {
  val PlayerField = Field[Any]("player")
  val TimeField = Field[Any]("time")
  val TeamField = Field[Any]("team")
  val OppositionTeamField = Field[Any]("oppositionTeam")
  val VenueField = Field[Any]("venue")
  val DateField = Field[Any]("date")
  val KickOffTimeField = Field[Any]("kickOffTime")
  val CompetitionField = Field[Any]("competition")
}

case class GoalsPage(tableState:TableState) extends SportPage {
  def withTableData(tableData:TableData) = GoalsPage(tableData.tableState)
}

object GoalsPageFactory extends PageFactory {
  def page = GoalsPage(TableState.Blank)
}

case class GoalsPageData(tableData:TableData) extends TablePageData