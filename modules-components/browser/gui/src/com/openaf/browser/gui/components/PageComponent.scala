package com.openaf.browser.gui.components

import com.openaf.browser.gui.{PageContext, Page, PageData}
import javafx.scene.layout.Region

trait PageComponent extends Region {
  protected var page:Page = _
  protected var pageData:PageData = _
  protected var pageContext:PageContext = _
  def initialise(page0:Page, pageData0:PageData, pageContext0:PageContext) {
    page = page0
    pageData = pageData0
    pageContext = pageContext0
    setup()
  }

  def setup()
}