package com.openaf.sport.api

import com.openaf.table.api.{TablePageData, TablePage}
import com.openaf.pagemanager.api.{PageFactory, ServerContext}
import com.openaf.table.lib.api.{Field, TableData, TableState}

trait SportPage extends TablePage {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[SportPageDataFacility])
}

object SportPage {
  val PlayerField = Field[String]("player")
  val TimeField = Field[Int]("time")
  val TeamField = Field[String]("team")
  val OppositionTeamField = Field[String]("oppositionTeam")
  val VenueField = Field[String]("venue")
  val DateField = Field[String]("date")
  val KickOffTimeField = Field[String]("kickOffTime")
  val CompetitionField = Field[String]("competition")
}

case class GoalsPage(tableState:TableState) extends SportPage {
  def withTableData(tableData:TableData) = GoalsPage(tableData.tableState)
}

object GoalsPageFactory extends PageFactory {
  def page = GoalsPage(TableState.Blank)
}

case class GoalsPageData(tableData:TableData) extends TablePageData