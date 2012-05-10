package com.openaf.browser

import javafx.scene.input.KeyCombination
import javafx.event.{ActionEvent, EventHandler}
import pages.HomePage
import javafx.scene.control._
import javafx.scene.layout.{Priority, HBox}

class BrowserBar(tabPane:BrowserTabPane, stage:BrowserStage) extends ToolBar {
  setStyle("-fx-background-color: #336699")

  private val backButton = new Button("Back")
  private val undoButton = new Button("Undo")
  private val redoButton = new Button("Redo")
  private val forwardButton = new Button("Forward")
  private val refreshButton = new Button("Refresh")
  private val homeButton = new Button("Home")
  private val addressBar = new AddressBar
  HBox.setHgrow(addressBar, Priority.ALWAYS)
  private val settingsButton = new SettingsMenuButton(tabPane, stage)

  getItems.addAll(backButton, undoButton, redoButton, forwardButton, refreshButton, homeButton, addressBar, settingsButton)
}

class SettingsMenuButton(tabPane:BrowserTabPane, stage:BrowserStage) extends MenuButton("Settings") {
  private val newTabMenuItem = new MenuItem("New Tab")
  newTabMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+T"))
  newTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(HomePage)}})

  private val newWindowMenuItem = new MenuItem("New Window")
  newWindowMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+N"))
  newWindowMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {stage.createStage(HomePage)}})

  getItems.addAll(newTabMenuItem, newWindowMenuItem)
}

class AddressBar extends TextField {

}