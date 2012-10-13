package com.openaf.browser.gui.components

import javafx.scene.layout.{FlowPane, BorderPane}
import com.openaf.browser.gui.pages.ManageCachesPage
import com.openaf.browser.gui.{PageContext, NoPageData}
import javafx.scene.control.{Button, Label}
import javafx.event.{ActionEvent, EventHandler}
import com.openaf.cache.{CacheChanged, CacheFactory}
import com.google.common.eventbus.Subscribe

class ManageCachesPageComponent extends BorderPane with PageComponent {
  type P = ManageCachesPage.type
  type PD = NoPageData.type

  CacheFactory.registerListener(this)

  private val content = new FlowPane

  private def updateContent() {
    val allCacheNames = CacheFactory.allCacheNames.toList.sorted
    val buttons = allCacheNames.map(cacheName => {
      val button = new Button("Clear " + cacheName + " Cache")
      button.setUserData(cacheName)
      button.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {CacheFactory.clearCache(button.getUserData.toString)}})
      button
    })
    content.getChildren.clear()
    content.getChildren.addAll(buttons.toArray :_*)
  }

  updateContent()

  setTop(new Label("Manage Caches"))
  setCenter(content)

  @Subscribe def cachesChanged(cacheChanged:CacheChanged) {updateContent()}

  def setup() {}
}

object ManageCachesPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new ManageCachesPageComponent
}
