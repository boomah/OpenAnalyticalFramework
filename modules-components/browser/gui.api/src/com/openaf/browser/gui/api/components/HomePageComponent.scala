package com.openaf.browser.gui.api.components

import javafx.scene.text.Text
import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui._
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import api.pages.HomePage
import scala.collection.JavaConversions._
import com.openaf.pagemanager.api.NoPageData
import api.utils.BrowserUtils
import com.openaf.browser.gui.api.{PageContext, OpenAFApplication, BrowserCacheKey}

class HomePageComponent(pageContext:PageContext) extends BorderPane with PageComponent {
  type P = HomePage.type
  type PD = NoPageData.type
  def name = "OpenAF"
  override val image = Some(BrowserUtils.icon("16x16_home.png"))

  private val browserApplications = pageContext.browserCache(BrowserCacheKey.BrowserApplicationsKeyWithDefault)
  browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
    def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications()}
  })
  private val content = new FlowPane
  private def updateBrowserApplications() {
    val applicationButtons = browserApplications.listIterator
      .filter(_.applicationButtons(pageContext).nonEmpty).map(application => {
      new OpenAFApplicationComponent(pageContext, application.applicationName, application.applicationButtons(pageContext))
    })
    content.getChildren.clear()
    content.getChildren.addAll(applicationButtons.toArray :_*)
  }
  updateBrowserApplications()

  setTop(new Text("HOME PAGE"))
  setCenter(content)

  def setup() {}
}

object HomePageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HomePageComponent(pageContext)
}