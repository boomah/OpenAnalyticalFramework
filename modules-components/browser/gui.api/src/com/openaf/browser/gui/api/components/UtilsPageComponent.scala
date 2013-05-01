package com.openaf.browser.gui.api.components

import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui.api.pages.UtilsPage
import javafx.scene.control.Label
import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change
import scala.collection.JavaConversions._
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.api.utils.BrowserUtils
import com.openaf.browser.gui.api.{PageContext, OpenAFApplication, BrowserCacheKey}

class UtilsPageComponent(pageContext:PageContext) extends BorderPane with PageComponent {
  type P = UtilsPage.type
  type PD = NoPageData.type
  def name = "Utils"
  override val image = Some(BrowserUtils.icon("16x16_home.png"))

  private val browserApplications = pageContext.browserCache(BrowserCacheKey.BrowserApplicationsKeyWithDefault)
  browserApplications.addListener(new ListChangeListener[OpenAFApplication] {
    def onChanged(change:Change[_<:OpenAFApplication]) {updateBrowserApplications()}
  })
  private val content = new FlowPane
  private def updateBrowserApplications() {
    val utilButtons = browserApplications.listIterator
      .filter(_.utilButtons(pageContext).nonEmpty).map(application => {
      new OpenAFApplicationComponent(pageContext, application.applicationName, application.utilButtons(pageContext))
    })
    content.getChildren.clear()
    content.getChildren.addAll(utilButtons.toArray :_*)
  }
  updateBrowserApplications()

  setTop(new Label("UTILS PAGE"))
  setCenter(content)

  def setup() {}
}

object UtilsPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new UtilsPageComponent(pageContext)
}