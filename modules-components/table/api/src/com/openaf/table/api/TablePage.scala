package com.openaf.table.api

import com.openaf.pagemanager.api.Page
import com.openaf.table.lib.api.TableData

trait TablePage extends Page {
  def withTableData(tableData:TableData):TablePage
}
