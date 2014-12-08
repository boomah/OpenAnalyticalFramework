package com.openaf.table.api

import com.openaf.pagemanager.api.Page
import com.openaf.table.lib.api.TableState

trait TablePage extends Page {
  def withTableState(tableState:TableState):TablePage
}
