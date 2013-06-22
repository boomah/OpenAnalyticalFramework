package com.openaf.sport

import com.openaf.sport.api.{GoalsPageData, GoalsPage, SportPageDataFacility}
import com.openaf.pagemanager.api.{NoPageData, Page}
import com.openaf.table.server.TableDataGenerator

class SportPageDataFacilityImpl extends SportPageDataFacility {
  private val goalsTableDataSource = new GoalsTableDataSource

  def pageData(page:Page) = {
    page match {
      case goalsPage:GoalsPage => goalsPageData(goalsPage)
      case _ => NoPageData
    }
  }

  private def goalsPageData(goalsPage:GoalsPage) = {
    val tableData = TableDataGenerator.tableData(goalsPage.tableState, goalsTableDataSource)
    GoalsPageData(tableData)
  }
}
