package com.openaf.table.api

import com.openaf.pagemanager.api.PageData
import com.openaf.table.lib.api.TableData

trait TablePageData extends PageData {
  def tableData:TableData
}
