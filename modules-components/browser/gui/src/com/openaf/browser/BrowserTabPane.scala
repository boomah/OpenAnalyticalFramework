package com.openaf.browser

import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.control.{Tab, TabPane}

class BrowserTabPane(initialPage:Page, stage:BrowserStage, manager:BrowserStageManager) extends TabPane {
  setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE)
  createTab(initialPage)

  def createTab(page:Page, goToNewTab:Boolean=true) {
    val browser = new Browser(page, this, stage, manager)
    val tab = new Tab(page.name)
    tab.setContent(browser)
    getTabs.add(getTabs.size(), tab)
    if (goToNewTab) {
      getSelectionModel.select(tab)
    }
  }

  def closeTab(tab:Tab) {
    if (getTabs.size > 1) {
      getTabs.remove(tab)
    } else {
      manager.closeBrowserStage(stage)
    }
  }

  setOnKeyTyped(new EventHandler[KeyEvent] {
    def handle(e:KeyEvent) {
      if ("w" == e.getCharacter && e.isShortcutDown) {
        closeTab(getSelectionModel.getSelectedItem)
      }
    }
  })
}
