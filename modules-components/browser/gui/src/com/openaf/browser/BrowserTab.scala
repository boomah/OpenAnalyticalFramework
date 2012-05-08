package com.openaf.browser

import javafx.scene.layout.HBox
import javafx.scene.image.{ImageView, Image}
import javafx.scene.control._
import javafx.event.{ActionEvent, EventHandler}
import pages.HomePage

class BrowserTab(name:String, image:Image, tabPane:BrowserTabPane) extends Tab {
  private val graphic = new BrowserTabGraphic(name, image, this, tabPane)
  setGraphic(graphic)
}

class BrowserTabGraphic(name:String, image:Image, tab:BrowserTab, tabPane:BrowserTabPane) extends HBox {
  private val imageView = new ImageView(image)
  private val closeTabEventHandler = new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.closeTab(tab)}}
  private val nameLabel = {
    val label = new Label(name, imageView)
    val newTabMenuItem = new MenuItem("New Tab")
    newTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(HomePage)}})
    val closeTabMenuItem = new MenuItem("Close Tab")
    closeTabMenuItem.setOnAction(closeTabEventHandler)
    val closeOtherTabsMenuItem = new MenuItem("Close Other Tabs")
    closeOtherTabsMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.closeOtherTabs(tab)}})
    val closeTabsToTheRightMenuItem = new MenuItem("Close Tabs to the Right")
    closeTabsToTheRightMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.closeTabsToTheRight(tab)}})
    val contextMenu = new ContextMenu(newTabMenuItem, new SeparatorMenuItem, closeTabMenuItem, closeOtherTabsMenuItem, closeTabsToTheRightMenuItem)
    label.setContextMenu(contextMenu)
    label
  }
  private val closeButton = {
    val button = new Button("X")
    button.setOnAction(closeTabEventHandler)
    button
  }
  getChildren.addAll(nameLabel, closeButton)
}