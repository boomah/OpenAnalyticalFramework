package com.openaf.browser.gui

import javafx.stage.Stage
import javafx.scene.Scene
import com.openaf.pagemanager.api.Page
import utils.BrowserUtils

class BrowserStage(homePage:Page, initialPage:Page, manager:BrowserStageManager) extends Stage {
  private val tabPane = new BrowserTabPane(homePage, initialPage, this, manager)
  private val scene = new Scene(tabPane)
  val cssResource = BrowserUtils.resource("/com/openaf/browser/gui/resources/openaf.css")
  println("CSS Resource:    " + cssResource)
  scene.getStylesheets.add(cssResource)
  setScene(scene)

  def createStage(initialPage:Page) {
    manager.createStage(this, initialPage)
  }
}
