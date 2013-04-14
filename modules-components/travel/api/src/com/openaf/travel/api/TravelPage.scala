package com.openaf.travel.api

import com.openaf.pagemanager.api.{PageData, PageFactory, ServerContext, Page}
import com.openaf.table.api._
import com.openaf.table.api.Field

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

  val ma = MeasureAreaLayout(TravelPage.CostField, List(TravelPage.HotelNameField, TravelPage.PeriodField)).measureAreaTrees.head
  val maaa = MeasureAreaLayout(List(ma, ma))


  val mat = MeasureAreaTree(Right(MeasureAreaLayout(ma)), ma.childMeasureAreaLayout)
  val mat2 = MeasureAreaTree(Right(MeasureAreaLayout(ma)), ma.childMeasureAreaLayout.copy(measureAreaTrees = ma.childMeasureAreaLayout.measureAreaTrees.reverse))

  import TravelPage._

  val topHeavy = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(CostField, StarRatingField))), MeasureAreaLayout(HotelNameField))).normalise

  val twoTopTwoBottom = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(CostField, StarRatingField))),
    MeasureAreaLayout.fromFields(List(HotelNameField, PeriodField)))).normalise

  val topBottom = MeasureAreaLayout(CostField, List(StarRatingField))


  def page = HotelsPage(TableState.Blank
    .withRowHeaderFields(List(TravelPage.HotelNameField, TravelPage.PeriodField))
//    .withMeasureAreaLayout(MeasureAreaLayout(TravelPage.CostField, List(TravelPage.HotelNameField, TravelPage.PeriodField)))
//    .withMeasureAreaLayout(MeasureAreaLayout.fromFields(List(TravelPage.HotelNameField, TravelPage.PeriodField)))
//    .withMeasureAreaLayout(maaa)
//    .withMeasureAreaLayout(MeasureAreaLayout(mat))
//    .withMeasureAreaLayout(MeasureAreaLayout(mat2))
//    .withMeasureAreaLayout(topHeavy)
//    .withMeasureAreaLayout(twoTopTwoBottom)
    .withMeasureAreaLayout(topBottom)
  )
}

object FlightsAndHotelsPageFactory extends PageFactory {
  def page = FlightsAndHotelsPage()
}

case class HotelsPageData(tableData:TableData) extends PageData