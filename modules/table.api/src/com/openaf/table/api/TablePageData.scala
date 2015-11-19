package com.openaf.table.api

import com.openaf.pagemanager.api.PageData
import com.openaf.table.lib.api.TableData

trait TablePageData extends PageData {
  def tableData:TableData
  def withTableData(tableData:TableData):TablePageData
}

case class StandardTablePageData(tableData:TableData) extends TablePageData {
  override def withTableData(tableData:TableData) = copy(tableData = tableData)
}