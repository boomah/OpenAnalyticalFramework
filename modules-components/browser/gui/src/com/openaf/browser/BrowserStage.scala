package com.openaf.browser

import javafx.stage.Stage
import javafx.scene.Scene

class BrowserStage(homePage:Page, initialPage:Page, manager:BrowserStageManager, pageBuilder:PageBuilder) extends Stage {
  private val tabPane = new BrowserTabPane(homePage, initialPage, this, manager, pageBuilder)
  private val scene = new Scene(tabPane)
  setScene(scene)

  def createStage(initialPage:Page) {
    manager.createStage(this, initialPage)
  }
}
