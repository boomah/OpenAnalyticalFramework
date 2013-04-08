package com.openaf.browser.gui.api.components

import javafx.scene.layout.Region
import com.openaf.pagemanager.api.{PageData, Page}
import javafx.scene.Node
import com.openaf.browser.gui.api.PageContext

trait PageComponent extends Region {
  type P <: Page
  type PD <: PageData
  val image:Option[Node]=None
  protected var page:P = _
  protected var pageData:PD = _
  protected var pageContext:PageContext = _

  def name:String
  def shortText = name
  def longText = name

  def initialise(page:P, pageData:PD, pageContext0:PageContext) {
    this.page = page
    this.pageData = pageData
    pageContext = pageContext0
    setup()
  }

  def setup()
}