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
  def withTableState(tableState:TableState) = HotelsPage(tableState)
}
case class FlightsAndHotelsPage() extends TravelPage {
  def withTableState(tableState:TableState) = this
}

object HotelsPageFactory extends PageFactory {

  val ma = ColumnHeaderLayout(TravelPage.CostField, List(TravelPage.HotelNameField, TravelPage.PeriodField)).columnHeaderTrees.head
  val maaa = ColumnHeaderLayout(List(ma, ma))


  val mat = ColumnHeaderTree(Right(ColumnHeaderLayout(ma)), ma.childColumnHeaderLayout)
  val mat2 = ColumnHeaderTree(Right(ColumnHeaderLayout(ma)), ma.childColumnHeaderLayout.copy(columnHeaderTrees = ma.childColumnHeaderLayout.columnHeaderTrees.reverse))

  import TravelPage._

  val topHeavy = ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout.fromFields(CostField, StarRatingField)), ColumnHeaderLayout(HotelNameField))).normalise

  val twoTopTwoBottom = ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout.fromFields(CostField, StarRatingField)),
    ColumnHeaderLayout.fromFields(HotelNameField, PeriodField))).normalise

  val topBottom = ColumnHeaderLayout(CostField, List(StarRatingField))


  def page = HotelsPage(TableState.Blank
    .withRowHeaderFields(List(TravelPage.HotelNameField, TravelPage.PeriodField))
//    .withColumnHeaderLayout(ColumnHeaderLayout(TravelPage.CostField, List(TravelPage.HotelNameField, TravelPage.PeriodField)))
//    .withColumnHeaderLayout(ColumnHeaderLayout.fromFields(TravelPage.HotelNameField, TravelPage.PeriodField))
//    .withColumnHeaderLayout(maaa)
//    .withColumnHeaderLayout(ColumnHeaderLayout(mat))
//    .withColumnHeaderLayout(ColumnHeaderLayout(mat2))
//    .withColumnHeaderLayout(topHeavy)
//    .withColumnHeaderLayout(twoTopTwoBottom)
    .withColumnHeaderLayout(topBottom)
  )
}

object FlightsAndHotelsPageFactory extends PageFactory {
  def page = FlightsAndHotelsPage()
}

case class HotelsPageData(tableData:TableData) extends TablePageData