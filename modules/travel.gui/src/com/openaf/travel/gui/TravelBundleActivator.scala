package com.openaf.travel.gui

import com.openaf.table.lib.api.{TableState, ColumnHeaderTree, ColumnHeaderLayout}
import components.HotelsPageComponentFactory
import org.osgi.framework.{BundleActivator, BundleContext}
import com.openaf.travel.api.{TravelPage, FlightsAndHotelsPage, HotelsPage}
import com.openaf.table.gui.OpenAFTable
import com.openaf.browser.gui.api.{PageFactory, BrowserContext, BrowserActionButton, OpenAFApplication}

class TravelBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("TravelBundleActivator gui started")
    context.registerService(classOf[OpenAFApplication], TravelBrowserApplication, null)
  }
  def stop(context:BundleContext) {
    println("TravelBundleActivator gui stopped")
  }
}

object TravelBrowserApplication extends OpenAFApplication {
  override def applicationButtons(context:BrowserContext) = {
    List(
      BrowserActionButton(HotelsPageComponentFactory.pageComponent.nameId, HotelsPageFactory),
      BrowserActionButton("Flights and Hotels", FlightsAndHotelsPageFactory)
    )
  }
  override def componentFactoryMap = Map(classOf[HotelsPage].getName -> HotelsPageComponentFactory)
  override def styleSheets = OpenAFTable.styleSheets
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
  def page = FlightsAndHotelsPage(TableState.Blank)
}