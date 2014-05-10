package com.openaf.travel.api

import com.openaf.pagemanager.api.{PageFactory, ServerContext}
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
  def withTableData(tableData:TableData) = HotelsPage(tableData.tableState)
}
case class FlightsAndHotelsPage() extends TravelPage {
  def withTableData(tableData:TableData) = this
}

object HotelsPageFactory extends PageFactory {

  val ma = MeasureAreaLayout(TravelPage.CostField, List(TravelPage.HotelNameField, TravelPage.PeriodField)).measureAreaTrees.head
  val maaa = MeasureAreaLayout(List(ma, ma))


  val mat = MeasureAreaTree(Right(MeasureAreaLayout(ma)), ma.childMeasureAreaLayout)
  val mat2 = MeasureAreaTree(Right(MeasureAreaLayout(ma)), ma.childMeasureAreaLayout.copy(measureAreaTrees = ma.childMeasureAreaLayout.measureAreaTrees.reverse))

  import TravelPage._

  val topHeavy = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(CostField, StarRatingField)), MeasureAreaLayout(HotelNameField))).normalise

  val twoTopTwoBottom = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(CostField, StarRatingField)),
    MeasureAreaLayout.fromFields(HotelNameField, PeriodField))).normalise

  val topBottom = MeasureAreaLayout(CostField, List(StarRatingField))


  def page = HotelsPage(TableState.Blank
    .withRowHeaderFields(List(TravelPage.HotelNameField, TravelPage.PeriodField))
//    .withMeasureAreaLayout(MeasureAreaLayout(TravelPage.CostField, List(TravelPage.HotelNameField, TravelPage.PeriodField)))
//    .withMeasureAreaLayout(MeasureAreaLayout.fromFields(TravelPage.HotelNameField, TravelPage.PeriodField))
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

case class HotelsPageData(tableData:TableData) extends TablePageData