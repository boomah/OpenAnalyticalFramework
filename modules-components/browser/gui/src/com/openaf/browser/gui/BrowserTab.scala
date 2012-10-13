package com.openaf.browser.gui

import javafx.scene.control._
import javafx.event.{ActionEvent, EventHandler}
import pages.HomePage
import javafx.scene.image.{Image, ImageView}
import javafx.beans.binding.{ObjectBinding, StringBinding, BooleanBinding}

class BrowserTab(name:StringBinding, image:ObjectBinding[Image], tabPane:BrowserTabPane) extends Tab {
  textProperty.bind(name)
  private val imageView = new ImageView
  imageView.imageProperty.bind(image)
  setGraphic(imageView)
  private val newTabMenuItem = new MenuItem("New Tab")
  newTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(HomePage)}})
  private val closeTabMenuItem = new MenuItem("Close Tab")
  closeTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.closeTab(BrowserTab.this)}})
  private val closeOtherTabsMenuItem = new MenuItem("Close Other Tabs")
  closeOtherTabsMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.closeOtherTabs(BrowserTab.this)}})
  private val closeTabsToTheRightMenuItem = new MenuItem("Close Tabs to the Right")
  closeTabsToTheRightMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.closeTabsToTheRight(BrowserTab.this)}})
  private val contextMenu = new ContextMenu(newTabMenuItem, new SeparatorMenuItem, closeTabMenuItem, closeOtherTabsMenuItem, closeTabsToTheRightMenuItem)
  setContextMenu(contextMenu)

  private val closeOtherTabsDisabled = new BooleanBinding {
    bind(tabPane.getTabs)
    def computeValue = (tabPane.getTabs.size <= 1)
  }
  closeOtherTabsMenuItem.disableProperty.bind(closeOtherTabsDisabled)
  private val closeTabsToTheRightDisabled = new BooleanBinding {
    bind(tabPane.getTabs)
    def computeValue = (tabPane.getTabs.indexOf(BrowserTab.this) == (tabPane.getTabs.size - 1))
  }
  closeTabsToTheRightMenuItem.disableProperty.bind(closeTabsToTheRightDisabled)

  tabPane.getTabs
}