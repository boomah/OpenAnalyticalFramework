package com.openaf.browser.gui.components

import javafx.scene.text.Text
import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui._
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import pages.HomePage
import scala.collection.JavaConversions._

class HomePageComponent(pageContext:PageContext) extends BorderPane with PageComponent {
  type P = HomePage.type
  type PD = NoPageData.type

  private val browserApplications = pageContext.browserCache(BrowserCacheKey.BrowserApplicationsKeyWithDefault)
  browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
    def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications()}
  })
  private val content = new FlowPane
  private def updateBrowserApplications() {
    val browserApplicationButtons = browserApplications.listIterator
      .filter(_.applicationButtons(pageContext).nonEmpty).map(application => {
      new OpenAFApplicationComponent(pageContext, application.applicationName, application.applicationButtons(pageContext))
    })
    content.getChildren.clear()
    browserApplicationButtons.foreach(component => {
      content.getChildren.add(component)
    })
  }
  updateBrowserApplications()

  setTop(new Text("HOME PAGE"))
  setCenter(content)

  def setup() {}
}

object HomePageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HomePageComponent(pageContext)
}