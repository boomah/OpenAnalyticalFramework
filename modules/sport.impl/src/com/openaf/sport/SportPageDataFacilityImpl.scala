package com.openaf.sport

import com.openaf.sport.api._
import com.openaf.pagemanager.api.{NoPageData, Page}

class SportPageDataFacilityImpl extends SportPageDataFacility {
  private val goalsTableDataSource = new GoalsTableDataSource
  private val runningTableDataSource = new RunningTableDataSource

  def pageData(page:Page) = {
    page match {
      case goalsPage:GoalsPage => goalsPageData(goalsPage)
      case runningPage:RunningPage => runningPageData(runningPage)
      case _ => NoPageData
    }
  }

  private def goalsPageData(goalsPage:GoalsPage) = {
    val tableData = goalsTableDataSource.tableData(goalsPage.tableState)
    GoalsPageData(tableData)
  }

  private def runningPageData(runningPage:RunningPage) = {
    val tableData = runningTableDataSource.tableData(runningPage.tableState)
    RunningPageData(tableData)
  }
}
