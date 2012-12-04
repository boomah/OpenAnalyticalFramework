package com.openaf.travel.api

import com.openaf.pagemanager.api.{PageFactory, ServerContext, Page}
import com.openaf.table.api.{Field, TableState}

trait TravelPage extends Page {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[TravelPageDataFacility])
}
object TravelPage {
  val CostField = new Field("cost")
  val PeriodField = new Field("period")
  val HotelNameField = new Field("hotelName")
  val StarRatingField = new Field("starRating")
}

case class HotelsPage(tableState:TableState) extends TravelPage
case class FlightsAndHotelsPage() extends TravelPage

object HotelsPageFactory extends PageFactory {
  def page = HotelsPage(TableState.Blank)
}

object FlightsAndHotelsPageFactory extends PageFactory {
  def page = FlightsAndHotelsPage()
}

