package com.openaf.table.gui.api

import com.openaf.browser.gui.api.{BrowserCacheKey, PageComponent}
import com.openaf.table.gui.OpenAFTable
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.TableData
import com.openaf.table.api.{TablePage, TablePageData}

trait TablePageComponent extends OpenAFTable with PageComponent {
  type PD <: TablePageData
  type P <: TablePage

  override def initialise() {
    localeProperty.bind(pageContext.browserCache(BrowserCacheKey.LocaleKey))
  }

  private var doingSetup = false

  def setup() {
    doingSetup = true
    setTableData(pageData.tableData)
    doingSetup = false
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {
      if (!doingSetup) {
        pageContext.goToPage(page.withTableData(newValue))
      }
    }
  })
}
