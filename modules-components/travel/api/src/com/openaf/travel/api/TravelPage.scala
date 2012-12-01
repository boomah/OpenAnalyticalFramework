package com.openaf.travel.api

import com.openaf.pagemanager.api.{PageFactory, ServerContext, Page}

trait TravelPage extends Page {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[TravelPageDataFacility])
}

case class HotelsPage() extends TravelPage
case class FlightsAndHotelsPage() extends TravelPage

object HotelsPageFactory extends PageFactory {
  def page = HotelsPage()
}

object FlightsAndHotelsPageFactory extends PageFactory {
  def page = FlightsAndHotelsPage()
}

