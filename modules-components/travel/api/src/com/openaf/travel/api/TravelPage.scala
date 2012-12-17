package com.openaf.travel.api

import com.openaf.pagemanager.api.{PageData, PageFactory, ServerContext, Page}
import com.openaf.table.api.{TableData, Field, TableState}

trait TravelPage extends Page {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[TravelPageDataFacility])
}
object TravelPage {
  val HotelNameField = new Field("hotelName")
  val PeriodField = new Field("period")
  val CostField = new Field("cost")
  val StarRatingField = new Field("starRating")
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