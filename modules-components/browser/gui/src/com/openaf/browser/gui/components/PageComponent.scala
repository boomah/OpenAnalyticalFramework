package com.openaf.browser.gui.components

import com.openaf.browser.gui.{PageContext, Page, PageData}
import javafx.scene.layout.Region

trait PageComponent extends Region {
  type P <: Page
  type PD <: PageData

  protected var page:P = _
  protected var pageData:PD = _
  protected var pageContext:PageContext = _

  def initialise(page:P, pageData:PD, pageContext0:PageContext) {
    this.page = page
    this.pageData = pageData
    pageContext = pageContext0
    setup()
  }

  def setup()
}