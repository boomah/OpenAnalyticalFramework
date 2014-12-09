package com.openaf.travel

import api.{HotelsPageData, FlightsAndHotelsPage, HotelsPage, TravelPageDataFacility}
import com.openaf.pagemanager.api.{NoPageData, Page}

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
    val tableData = hotelsTableDataSource.tableData(hotelsPage.tableState)
    HotelsPageData(tableData)
  }

  private def flightsAndHotelsPageData(flightsAndHotelsPage:FlightsAndHotelsPage) = {
    NoPageData
  }
}
