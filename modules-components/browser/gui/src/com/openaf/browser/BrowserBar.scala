package com.openaf.browser

import javafx.scene.layout.HBox
import javafx.scene.control.{MenuItem, MenuButton, Label}
import javafx.scene.input.KeyCombination
import javafx.event.{ActionEvent, EventHandler}
import pages.HomePage

class BrowserBar(tabPane:BrowserTabPane, stage:BrowserStage) extends HBox {
  setStyle("-fx-background-color: #336699")
  getChildren.add(new Label("Hello"))

  private val settingsButton = new SettingsMenuButton(tabPane, stage)

  getChildren.add(settingsButton)
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