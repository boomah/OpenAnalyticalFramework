package com.openaf.browser

import javafx.scene.layout.BorderPane

class Browser(initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager) extends BorderPane {
  private val browserBar = new BrowserBar(tabPane, stage)
  setTop(browserBar)
}
