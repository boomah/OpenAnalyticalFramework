package com.openaf.testdata

import com.openaf.osgi.OSGIUtils
import com.openaf.pagemanager.api.{NoPageData, Page}
import com.openaf.table.api.{StandardTablePageData, TablePage}
import com.openaf.testdata.api.TestDataTablePageDataFacility
import org.osgi.framework.{BundleActivator, BundleContext}

class TestDataBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("TestDataBundleActivator started")
    val dictionary = OSGIUtils.mapToDictionary(Map(OSGIUtils.ExportService -> true))
    context.registerService(classOf[TestDataTablePageDataFacility], new TestDataTablePageDataFacilityImpl, dictionary)
  }
  def stop(context:BundleContext) {
    println("TestDataBundleActivator stopped")
  }
}

class TestDataTablePageDataFacilityImpl extends TestDataTablePageDataFacility {
  private val tableDataSource = new TestDataTableDataSource

  def pageData(tablePage:TablePage) = StandardTablePageData(tableDataSource.tableData(tablePage.tableState))

  override def pageData(page:Page) = {
    page match {
      case tablePage:TablePage => pageData(tablePage)
      case _ => NoPageData
    }
  }
}
