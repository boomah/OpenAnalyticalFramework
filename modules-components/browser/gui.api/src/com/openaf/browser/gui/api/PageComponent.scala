package com.openaf.browser.gui.api

import javafx.scene.layout.Region
import com.openaf.pagemanager.api.{PageData, Page}
import javafx.scene.Node

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

  def initialise(pageContext0:PageContext) {
    pageContext = pageContext0
    initialise()
  }
  def initialise() {}

  def setup(page:P, pageData:PD) {
    this.page = page
    this.pageData = pageData
    setup()
  }
  def setup()
}