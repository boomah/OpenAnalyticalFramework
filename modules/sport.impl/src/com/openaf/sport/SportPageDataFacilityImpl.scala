package com.openaf.sport

import com.openaf.sport.api.{GoalsPageData, GoalsPage, SportPageDataFacility}
import com.openaf.pagemanager.api.{NoPageData, Page}

class SportPageDataFacilityImpl extends SportPageDataFacility {
  private val goalsTableDataSource = new GoalsTableDataSource

  def pageData(page:Page) = {
    page match {
      case goalsPage:GoalsPage => goalsPageData(goalsPage)
      case _ => NoPageData
    }
  }

  private def goalsPageData(goalsPage:GoalsPage) = {
    val tableData = goalsTableDataSource.tableData(goalsPage.tableState)
    GoalsPageData(tableData)
  }
}
