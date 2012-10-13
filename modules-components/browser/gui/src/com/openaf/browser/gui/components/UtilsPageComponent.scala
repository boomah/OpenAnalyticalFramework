package com.openaf.browser.gui.components

import com.openaf.browser.gui.{OpenAFApplication, BrowserCacheKey, NoPageData, PageContext}
import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui.pages.UtilsPage
import javafx.scene.control.Label
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import scala.collection.JavaConversions._

class UtilsPageComponent(pageContext:PageContext) extends BorderPane with PageComponent {
  type P = UtilsPage.type
  type PD = NoPageData.type

  private val browserApplications = pageContext.browserCache(BrowserCacheKey.BrowserApplicationsKeyWithDefault)
  browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
    def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications()}
  })
  private val content = new FlowPane
  private def updateBrowserApplications() {
    val browserApplicationButtons = browserApplications.listIterator
      .filter(_.utilButtons(pageContext).nonEmpty).map(application => {
      new OpenAFApplicationComponent(pageContext, application.applicationName, application.utilButtons(pageContext))
    })
    content.getChildren.clear()
    browserApplicationButtons.foreach(component => {
      content.getChildren.add(component)
    })
  }
  updateBrowserApplications()

  setTop(new Label("UTILS PAGE"))
  setCenter(content)

  def setup() {}
}

object UtilsPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new UtilsPageComponent(pageContext)
}
