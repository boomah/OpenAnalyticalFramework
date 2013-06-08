package com.openaf.travel.gui.components

import com.openaf.browser.gui.api.components.{PageComponentFactory, PageComponent}
import com.openaf.travel.api.{HotelsPageData, HotelsPage}
import com.openaf.table.gui.OpenAFTable
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.api.TableData
import com.openaf.browser.gui.api.BrowserCacheKey

class HotelsPageComponent extends OpenAFTable with PageComponent {
  type P = HotelsPage
  type PD = HotelsPageData

  def name = "Hotels"

  private var doingSetup = false

  override def initialise() {
    localeProperty.bind(pageContext.browserCache(BrowserCacheKey.LocaleKey))
  }

  def setup() {
    doingSetup = true
    setTableData(pageData.tableData)
    doingSetup = false
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {
      if (!doingSetup) {
        println("HotelsPageComponent going to new page")
        pageContext.goToPage(HotelsPage(newValue.tableState))
      }
    }
  })
}

object HotelsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new HotelsPageComponent
}