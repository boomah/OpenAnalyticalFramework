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
  browserApplications.addListener(new ListChangeListener[BrowserApplication] {
    def onChanged(change:Change[_<:BrowserApplication]) {updateBrowserApplications()}
  })
  private val content = new FlowPane
  private def updateBrowserApplications() {
    val browserApplicationButtons = browserApplications.listIterator.map(new BrowserApplicationComponent(pageContext, _))
    content.getChildren.clear()
    browserApplicationButtons.foreach(component => {
      content.getChildren.add(component)
    })
  }
  updateBrowserApplications()

  setTop(new Text("Nick"))
  setCenter(content)

  def setup() {}
}

object HomePageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HomePageComponent(pageContext)
}