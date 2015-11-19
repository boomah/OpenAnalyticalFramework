package com.openaf.travel.api

import com.openaf.pagemanager.api.ServerContext
import com.openaf.table.lib.api._
import com.openaf.table.lib.api.Field
import com.openaf.table.api.{TablePageData, TablePage}

trait TravelPage extends TablePage {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[TravelPageDataFacility])
}
object TravelPage {
  val HotelNameField = Field[Any]("hotelName")
  val PeriodField = Field[Any]("period")
  val CostField = Field[Any]("cost")
  val StarRatingField = Field[Any]("starRating")
}

case class HotelsPage(tableState:TableState) extends TravelPage {
  def withTableState(tableState:TableState) = HotelsPage(tableState)
}

case class FlightsAndHotelsPage(tableState:TableState) extends TravelPage {
  def withTableState(tableState:TableState) = FlightsAndHotelsPage(tableState)
}

case class HotelsPageData(tableData:TableData) extends TablePageData {
  override def withTableData(tableData:TableData) = copy(tableData = tableData)
}