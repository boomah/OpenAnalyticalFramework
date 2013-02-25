package com.openaf.travel.api

import com.openaf.pagemanager.api.{PageData, PageFactory, ServerContext, Page}
import com.openaf.table.api.{TableData, Field, TableState}

trait TravelPage extends Page {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[TravelPageDataFacility])
}
object TravelPage {
  val HotelNameField = Field("hotelName")
  val PeriodField = Field("period")
  val CostField = Field("cost")
  val StarRatingField = Field("starRating")
}

case class HotelsPage(tableState:TableState) extends TravelPage
case class FlightsAndHotelsPage() extends TravelPage

object HotelsPageFactory extends PageFactory {
  def page = HotelsPage(TableState.Blank.withRowHeaderFields(List(TravelPage.HotelNameField, TravelPage.PeriodField)))
}

object FlightsAndHotelsPageFactory extends PageFactory {
  def page = FlightsAndHotelsPage()
}

case class HotelsPageData(tableData:TableData) extends PageData