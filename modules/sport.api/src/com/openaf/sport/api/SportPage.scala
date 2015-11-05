package com.openaf.sport.api

import java.time.{LocalDate, Duration}

import com.openaf.table.api.{TablePageData, TablePage}
import com.openaf.pagemanager.api.ServerContext
import com.openaf.table.lib.api._

trait SportPage extends TablePage {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[SportPageDataFacility])
}

object SportPage {
  val PlayerField = Field[String]("player")
  val StartTimeField = Field[Int]("startTime")
  val TeamField = Field[String]("team")
  val OppositionTeamField = Field[String]("oppositionTeam")
  val VenueField = Field[String]("venue")
  val DateField = Field[LocalDate]("date")
  val KickOffTimeField = Field[String]("kickOffTime")
  val CompetitionField = Field[String]("competition")

  val LocationField = Field[String]("location")
  val NumberField = Field[Integer]("number")
  // Date Field
  val PositionField = Field[Integer]("position", MultipleFieldType(Dimension))
  val NameField = Field[String]("name")
  val TimeField = Field[Duration]("time", MultipleFieldType(Dimension))
  val AgeCatField = Field[String]("ageCat")
  val AgeGradeField = Field[String]("ageGrade")
  val GenderField = Field[String]("gender")
  val GenderPosField = Field[Integer]("genderPos", MultipleFieldType(Dimension))
  val ClubField = Field[String]("club")
  val NoteField = Field[String]("noteField")
}

case class GoalsPage(tableState:TableState) extends SportPage {
  def withTableState(tableState:TableState) = GoalsPage(tableState)
}

case class GoalsPageData(tableData:TableData) extends TablePageData

case class RunningPage(tableState:TableState) extends SportPage {
  def withTableState(tableState:TableState) = RunningPage(tableState)
}

case class RunningPageData(tableData:TableData) extends TablePageData