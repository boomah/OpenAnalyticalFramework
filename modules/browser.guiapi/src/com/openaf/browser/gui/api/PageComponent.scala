package com.openaf.browser.gui.api

import javafx.scene.layout.Region
import com.openaf.pagemanager.api.{PageData, Page}
import javafx.scene.Node
import java.util.ResourceBundle
import javafx.beans.binding.StringBinding

trait PageComponent extends Region {
  getStyleClass.add("page-component")

  type P <: Page
  type PD <: PageData

  private var page0:P = _
  private var pageData0:PD = _
  private var context0:BrowserContext = _
  private var application0:OpenAFApplication = _

  final protected def page = page0
  final protected def pageData = pageData0
  final protected def context = context0

  protected def resourceLocation = application0.resourceLocation
  protected def resourceClassLoader = Option(application0).map(_.getClass.getClassLoader).getOrElse(getClass.getClassLoader)
  protected def textFromResource(id:String, location:String=resourceLocation, classLoader:ClassLoader=resourceClassLoader) = {
    val bundle = ResourceBundle.getBundle(location, context.cache(BrowserCacheKey.LocaleKey).getValue, classLoader)
    if (bundle.containsKey(id)) bundle.getString(id) else id
  }
  protected def stringBindingFromResource(id:String, location:String=resourceLocation, classLoader:ClassLoader=resourceClassLoader) = {
    new StringBinding {
      bind(context.cache(BrowserCacheKey.LocaleKey))
      def computeValue = textFromResource(id, location, classLoader)
    }
  }

  def nameId:String
  def name = textFromResource(nameId)
  def descriptionId = nameId
  def description = textFromResource(descriptionId)
  def image:Option[Node]=None
  def providesTopBorder = false

  final private[api] def initialise(context:BrowserContext, application:OpenAFApplication) {
    context0 = context
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