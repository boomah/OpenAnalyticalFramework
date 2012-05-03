package com.openaf.browser

import javafx.stage.Stage
import javafx.scene.Scene

class BrowserStage(initialPage:Page, manager:BrowserStageManager) extends Stage {
  private val tabPane = new BrowserTabPane(initialPage, this, manager)
  private val scene = new Scene(tabPane)
  setScene(scene)

  def createStage(initialPage:Page) {
    manager.createStage(this, initialPage)
  }
}
