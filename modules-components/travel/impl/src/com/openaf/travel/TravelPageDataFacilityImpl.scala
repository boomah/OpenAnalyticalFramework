package com.openaf.travel

import api.{FlightsAndHotelsPage, HotelsPage, TravelPageDataFacility}
import com.openaf.pagemanager.api.{NoPageData, Page}
import com.openaf.table.server.TableDataGenerator

class TravelPageDataFacilityImpl extends TravelPageDataFacility {
  private val hotelsTableDataSource = new HotelsTableDataSource

  def pageData(page:Page) = {
    page match {
      case hotelsPage:HotelsPage => hotelsPageData(hotelsPage)
      case flightsAndHotelsPage:FlightsAndHotelsPage => flightsAndHotelsPageData(flightsAndHotelsPage)
      case _ => NoPageData
    }
  }

  private def hotelsPageData(hotelsPage:HotelsPage) = {
    val tableData = TableDataGenerator.tableData(hotelsPage.tableState, hotelsTableDataSource)
    NoPageData
  }

  private def flightsAndHotelsPageData(flightsAndHotelsPage:FlightsAndHotelsPage) = {
    NoPageData
  }
}
