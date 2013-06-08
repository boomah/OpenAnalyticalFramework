package com.openaf.browser.gui.api.components

import javafx.scene.text.Text
import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui._
import javafx.collections.{ObservableList, ListChangeListener}
import javafx.collections.ListChangeListener.Change
import api.pages.HomePage
import scala.collection.JavaConversions._
import com.openaf.pagemanager.api.NoPageData
import api.utils.BrowserUtils
import com.openaf.browser.gui.api.{OpenAFApplication, BrowserCacheKey}

class HomePageComponent extends BorderPane with PageComponent {
  type P = HomePage.type
  type PD = NoPageData.type
  def name = "OpenAF"
  override val image = Some(BrowserUtils.icon("16x16_home.png"))

  override def initialise() {
    val browserApplications = pageContext.browserCache(BrowserCacheKey.ApplicationsKey)
    browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
      def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications(browserApplications)}
    })
    updateBrowserApplications(browserApplications)
  }

  private val content = new FlowPane
  private def updateBrowserApplications(browserApplications:ObservableList[OpenAFApplication]) {
    val applicationButtons = browserApplications.filter(_.applicationButtons(pageContext).nonEmpty).map(application => {
      new OpenAFApplicationComponent(pageContext, application.applicationName, application.applicationButtons(pageContext))
    })
    content.getChildren.clear()
    content.getChildren.addAll(applicationButtons.toArray :_*)
  }

  setTop(new Text("HOME PAGE"))
  setCenter(content)

  def setup() {}
}

object HomePageComponentFactory extends PageComponentFactory {
  def pageComponent = new HomePageComponent
}