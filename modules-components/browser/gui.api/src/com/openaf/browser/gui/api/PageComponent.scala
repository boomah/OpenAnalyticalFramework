package com.openaf.browser.gui.api

import javafx.scene.layout.Region
import com.openaf.pagemanager.api.{PageData, Page}
import javafx.scene.Node

trait PageComponent extends Region {
  type P <: Page
  type PD <: PageData
  val image:Option[Node]=None

  private var page0:P = _
  private var pageData0:PD = _
  private var pageContext0:PageContext = _
  private var application0:OpenAFApplication = _

  final protected def page = page0
  final protected def pageData = pageData0
  final protected def pageContext = pageContext0

  protected def resourceLocation = application0.resourceLocation

  def name:String
  def shortText = name
  def longText = name

  final private[api] def initialise(pageContext:PageContext, application:OpenAFApplication) {
    pageContext0 = pageContext
    application0 = application
    initialise()
  }
  protected def initialise() {}

  final def setup(page:P, pageData:PD) {
    page0 = page
    pageData0 = pageData
    setup()
  }
  protected def setup()
}