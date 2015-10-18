package com.openaf.testdata

import com.openaf.table.api.{StandardTablePageData, TablePage, TablePageDataFacility}
import com.openaf.table.server.api.TableBundleActivator
import com.openaf.testdata.api.TestData

class TestDataBundleActivator extends TableBundleActivator {
  override def tablePageDataFacility:TablePageDataFacility = new TestDataTablePageDataFacility
}

class TestDataTablePageDataFacility extends TablePageDataFacility {
  private val tableDataSource = new TestDataTableDataSource

  override def nameId = TestData.NameId
  override def pageData(tablePage:TablePage) = StandardTablePageData(tableDataSource.tableData(tablePage.tableState))
}
