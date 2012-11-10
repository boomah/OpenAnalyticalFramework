package com.openaf.browser.gui.components

import javafx.scene.layout.Region
import com.openaf.pagemanager.api.{PageData, Page}
import com.openaf.browser.gui.PageContext
import javafx.scene.image.Image

trait PageComponent extends Region {
  type P <: Page
  type PD <: PageData
  val image:Option[Image]=None
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