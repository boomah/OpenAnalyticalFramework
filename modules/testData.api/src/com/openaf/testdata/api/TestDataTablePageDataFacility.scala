package com.openaf.testdata.api

import com.openaf.pagemanager.api.{PageDataFacility, ServerContext}
import com.openaf.table.api.TablePage
import com.openaf.table.lib.api.{Field, TableState}

trait TestDataTablePageDataFacility extends PageDataFacility

object TestDataTablePageDataFacility {
  val NameId = "testDataName"

  val IdField = Field[Int]("id")
  val PersonField = Field[StringWrapper]("person")
}

case class TestDataPage(tableState:TableState) extends TablePage {
  def withTableState(tableState:TableState) = TestDataPage(tableState)
  override def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[TestDataTablePageDataFacility])
}