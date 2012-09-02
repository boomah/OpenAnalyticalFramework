package com.openaf.browser.gui

import javafx.event.{ActionEvent, EventHandler}
import pages.HomePage
import javafx.scene.control._
import javafx.scene.layout.{Priority, HBox}
import javafx.beans.binding.BooleanBinding
import utils.BrowserUtils
import javafx.beans.property.SimpleBooleanProperty
import java.lang.Boolean
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}

class BrowserBar(browser:Browser, tabPane:BrowserTabPane, stage:BrowserStage) extends ToolBar {
  setStyle("-fx-background-color: #336699")

  private val backButton = new SingleActionToolBarButton("Back", browser.back, browser.backAndUndoDisabledProperty)
  private val forwardButton = new SingleActionToolBarButton("Forward", browser.forward, browser.redoAndForwardDisabledProperty)
  private val stopOrRefreshButton = new StopOrRefreshToolBarButton("Stop", (isStop) => browser.stopOrRefresh(isStop),
                                                                   browser.stopOrRefreshDisabledProperty, browser.working)
  private val homeButton = new SingleActionToolBarButton("Home", browser.home, browser.homeDisabledProperty)
  private val addressBar = new AddressBar
  HBox.setHgrow(addressBar, Priority.ALWAYS)
  private val settingsButton = new SettingsMenuButton(tabPane, stage)

  getItems.addAll(backButton, forwardButton, stopOrRefreshButton, homeButton, addressBar, settingsButton)
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

class ToolBarButton(text:String, disabled:BooleanBinding) extends Button(text) {
  setFocusTraversable(false)
  disableProperty.bind(disabled)
}

class SingleActionToolBarButton(text:String, action: ()=>Unit, disabled:BooleanBinding) extends ToolBarButton(text, disabled) {
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action()}})
}

class StopOrRefreshToolBarButton(text:String, action: (Boolean)=>Unit, disabled:BooleanBinding,
                                 working:SimpleBooleanProperty) extends ToolBarButton(text, disabled) {
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action(working.get)}})
  working.addListener(new ChangeListener[JBoolean] {
    def changed(observable:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
      if (newValue) setText("Stop") else setText("Refresh")
    }
  })
}