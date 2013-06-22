package com.openaf.sport.api

import com.openaf.table.api.{TablePageData, TablePage}
import com.openaf.pagemanager.api.{PageFactory, ServerContext}
import com.openaf.table.lib.api.{Field, TableData, TableState}

trait SportPage extends TablePage {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[SportPageDataFacility])
}

object SportPage {
  val PlayerField = Field("player")
  val TimeField = Field("time")
  val TeamField = Field("team")
  val OppositionTeamField = Field("oppositionTeam")
  val VenueField = Field("venue")
  val DateField = Field("date")
  val KickOffTimeField = Field("kickOffTime")
  val CompetitionField = Field("competition")
}

case class GoalsPage(tableState:TableState) extends SportPage {
  def withTableData(tableData:TableData) = GoalsPage(tableData.tableState)
}

object GoalsPageFactory extends PageFactory {
  def page = GoalsPage(TableState.Blank)
}

case class GoalsPageData(tableData:TableData) extends TablePageData