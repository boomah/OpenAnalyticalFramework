package com.openaf.browser.gui.components

import javafx.scene.text.Text
import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui._
import javafx.collections.{ObservableList, ListChangeListener}
import javafx.collections.ListChangeListener.Change
import pages.HomePage
import scala.collection.JavaConversions._
import com.openaf.pagemanager.api.NoPageData
import utils.BrowserUtils
import com.openaf.browser.gui.api._
import com.openaf.browser.gui.binding.ApplicationLocaleStringBinding

class HomePageComponent extends BorderPane with BrowserPageComponent {
  type P = HomePage.type
  type PD = NoPageData.type
  def nameId = "openAFName"
  override val image = Some(BrowserUtils.icon("16x16_home.png"))

  override def initialise() {
    val browserApplications = pageContext.browserCache(InternalBrowserCacheKey.ApplicationsKey)
    browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
      def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications(browserApplications)}
    })
    updateBrowserApplications(browserApplications)
  }

  private val content = new FlowPane
  private def updateBrowserApplications(browserApplications:ObservableList[OpenAFApplication]) {
    val applicationButtons = browserApplications.filter(_.applicationButtons(pageContext).nonEmpty).map(application => {
      val nameBinding = new ApplicationLocaleStringBinding(BrowserUtils.ApplicationName, application, pageContext.browserCache)
      new OpenAFApplicationComponent(pageContext, nameBinding, application.applicationButtons(pageContext))
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