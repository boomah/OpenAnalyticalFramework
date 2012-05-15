package com.openaf.browser

import javafx.scene.control.{Tab, TabPane}
import javafx.event.{Event, EventHandler}
import javafx.scene.input.KeyEvent
import utils.BrowserUtils

class BrowserTabPane(initialPage:Page, stage:BrowserStage, manager:BrowserStageManager) extends TabPane {
  setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS)
  createTab(initialPage)

  def createTab(page:Page, goToNewTab:Boolean=true) {
    val browser = new Browser(page, this, stage, manager)
    val tab = new BrowserTab(page.name, page.image, this)
    tab.setContent(browser)
    tab.setOnClosed(new EventHandler[Event] {def handle(e:Event) {ensureTabSelected()}})
    getTabs.add(getTabs.size(), tab)
    if (goToNewTab) {
      getSelectionModel.select(tab)
    }
  }

  def closeTab(tab:Tab) {
    if (getTabs.size > 1) {
      getTabs.remove(tab)
      ensureTabSelected()
    } else {
      manager.closeBrowserStage(stage)
    }
  }

  def closeOtherTabs(tabToKeepOpen:Tab) {
    getTabs.retainAll(tabToKeepOpen)
    ensureTabSelected()
  }

  def closeTabsToTheRight(tab:Tab) {
    val indexOfTab = getTabs.indexOf(tab)
    getTabs.subList(indexOfTab + 1, getTabs.size).clear()
    ensureTabSelected()
  }

  private def ensureTabSelected() {
    if (getSelectionModel.getSelectedIndex >= getTabs.size) {
      getSelectionModel.select(getTabs.size - 1)
    }
  }

  private def currentBrowser = getSelectionModel.getSelectedItem.getContent.asInstanceOf[Browser]

  setOnKeyPressed(new EventHandler[KeyEvent] {
      def handle(e:KeyEvent) {
        val km = BrowserUtils.keyMap
        if (km.closeTab.matches(e)) closeTab(getSelectionModel.getSelectedItem)
        else if (km.pageBack.matches(e)) currentBrowser.back()
        else if (km.undo.matches(e)) currentBrowser.undo()
        else if (km.redo.matches(e)) currentBrowser.redo()
        else if (km.pageForward.matches(e)) currentBrowser.forward()
      }
  })
}
