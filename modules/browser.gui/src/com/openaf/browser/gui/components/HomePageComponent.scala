package com.openaf.browser.gui.components

import javafx.scene.text.Text
import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui._
import javafx.collections.{ObservableList, ListChangeListener}
import javafx.collections.ListChangeListener.Change
import pages.HomePage
import scala.collection.JavaConversions._
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.api._

class HomePageComponent extends BorderPane with BrowserPageComponent {
  type P = HomePage.type
  type PD = NoPageData.type
  def nameId = "openAFName"

  override def initialise() {
    val browserApplications = context.cache(InternalBrowserCacheKey.ApplicationsKey)
    browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
      def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications(browserApplications)}
    })
    updateBrowserApplications(browserApplications)
  }

  private val content = new FlowPane
  private def updateBrowserApplications(browserApplications:ObservableList[OpenAFApplication]) {
    val applicationButtons = browserApplications.filter(_.applicationButtons(context).nonEmpty).map(application => {
      new OpenAFApplicationComponent(application, context)
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