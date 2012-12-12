package com.openaf.travel.gui.components

import com.openaf.browser.gui.components.{PageComponentFactory, PageComponent}
import com.openaf.travel.api.{HotelsPageData, HotelsPage}
import com.openaf.browser.gui.PageContext

class HotelsPageComponent extends PageComponent {
  type P = HotelsPage
  type PD = HotelsPageData

  def name = "Hotels"
  def setup() {
    val tableState = pageData.tableData.tableState
    println("TableState : " + tableState)
  }
}

object HotelsPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HotelsPageComponent
}