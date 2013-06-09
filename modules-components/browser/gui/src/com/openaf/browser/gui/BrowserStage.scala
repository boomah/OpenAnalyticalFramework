package com.openaf.browser.gui

import javafx.stage.Stage
import javafx.scene.Scene
import com.openaf.pagemanager.api.Page
import com.openaf.browser.gui.utils.BrowserUtils._

class BrowserStage(homePage:Page, initialPage:Page, manager:BrowserStageManager) extends Stage {
  private val tabPane = new BrowserTabPane(homePage, initialPage, this, manager)
  private val scene = new Scene(tabPane)
  scene.getStylesheets.add(resource("/com/openaf/browser/gui/resources/openaf.css"))
  setScene(scene)

  def createStage(initialPage:Page) {
    manager.createStage(this, initialPage)
  }
}
