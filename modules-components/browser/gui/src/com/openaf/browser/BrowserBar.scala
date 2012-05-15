package com.openaf.browser

import javafx.event.{ActionEvent, EventHandler}
import pages.HomePage
import javafx.scene.control._
import javafx.scene.layout.{Priority, HBox}
import javafx.beans.binding.BooleanBinding
import utils.BrowserUtils

class BrowserBar(browser:Browser, tabPane:BrowserTabPane, stage:BrowserStage) extends ToolBar {
  setStyle("-fx-background-color: #336699")

  private val backButton = new ToolBarButton("Back", browser.back, browser.backAndUndoDisabledProperty)
  private val undoButton = new ToolBarButton("Undo", browser.undo, browser.backAndUndoDisabledProperty)
  private val redoButton = new ToolBarButton("Redo", browser.redo, browser.redoAndForwardDisabledProperty)
  private val forwardButton = new ToolBarButton("Forward", browser.forward, browser.redoAndForwardDisabledProperty)
  private val refreshButton = new ToolBarButton("Refresh", browser.refresh, browser.refreshDisabledProperty)
  private val homeButton = new ToolBarButton("Home", browser.home, browser.homeDisabledProperty)
  private val addressBar = new AddressBar
  HBox.setHgrow(addressBar, Priority.ALWAYS)
  private val settingsButton = new SettingsMenuButton(tabPane, stage)

  getItems.addAll(backButton, undoButton, redoButton, forwardButton, refreshButton, homeButton, addressBar, settingsButton)
}

class SettingsMenuButton(tabPane:BrowserTabPane, stage:BrowserStage) extends MenuButton("Settings") {
  private val newTabMenuItem = new MenuItem("New Tab")
  newTabMenuItem.setAccelerator(BrowserUtils.keyMap.newTab.accelerator)
  newTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(HomePage)}})

  private val newWindowMenuItem = new MenuItem("New Window")
  newWindowMenuItem.setAccelerator(BrowserUtils.keyMap.newWindow.accelerator)
  newWindowMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {stage.createStage(HomePage)}})

  getItems.addAll(newTabMenuItem, newWindowMenuItem)
}

class AddressBar extends TextField {

}

class ToolBarButton(text:String, action: () => Unit, disabled:BooleanBinding) extends Button(text) {
  setFocusTraversable(false)
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action()}})
  disableProperty.bind(disabled)
}