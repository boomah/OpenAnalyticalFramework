package com.openaf.travel.gui.components

import com.openaf.browser.gui.components.{PageComponentFactory, PageComponent}
import com.openaf.travel.api.{HotelsPageData, HotelsPage}
import com.openaf.browser.gui.PageContext
import com.openaf.table.gui.OpenAFTable

class HotelsPageComponent extends OpenAFTable with PageComponent {
  type P = HotelsPage
  type PD = HotelsPageData

  def name = "Hotels"
  def setup() {
    println("Page Data : " + pageData)
    setTableData(pageData.tableData)
  }
}

object HotelsPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HotelsPageComponent
}