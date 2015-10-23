package com.openaf.table.api

import com.openaf.pagemanager.api.{Page, PageDataFacility, ServerContext}
import com.openaf.table.lib.api.TableState

trait TablePage extends Page {
  def tableState:TableState
  def withTableState(tableState:TableState):TablePage
}

case class StandardTablePage(override val nameId:String, tableState:TableState) extends TablePage {
  override def pageDataFacility(serverContext:ServerContext) = {
    val facilities = serverContext.facilities(classOf[TablePageDataFacility])
    facilities.find(_.nameId == nameId).get
  }
  override def withTableState(tableState:TableState) = StandardTablePage(nameId, tableState)
}

trait TablePageDataFacility extends PageDataFacility {
  def nameId:String
  def pageData(tablePage:TablePage):TablePageData
}