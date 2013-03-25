package com.openaf.browser.gui

import javafx.event.{ActionEvent, EventHandler}
import pages.{HomePage, UtilsPage}
import javafx.scene.control._
import javafx.scene.layout.{Priority, HBox}
import javafx.beans.binding.{StringBinding, BooleanBinding}
import utils.BrowserUtils._
import javafx.beans.property.SimpleBooleanProperty
import java.lang.Boolean
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}
import utils.{FontAwesomeLabel, FontAwesome}, FontAwesome._
import javafx.beans.{Observable, InvalidationListener}

class BrowserBar(browser:Browser, tabPane:BrowserTabPane, stage:BrowserStage) extends ToolBar {
  getStyleClass.add("browser-bar")

  private val backButton = new SingleActionToolBarButton(ArrowLeft, browser.back, browser.backDisabledProperty)
  private val forwardButton = new SingleActionToolBarButton(ArrowRight, browser.forward, browser.forwardDisabledProperty)
  private val stopOrRefreshButton = new StopOrRefreshToolBarButton(Remove, (isStop) => browser.stopOrRefresh(isStop),
                                                                   browser.stopOrRefreshDisabledProperty, browser.working)
  private val homeButton = new SingleActionToolBarButton(Home, browser.home, browser.homeDisabledProperty)
  private val navigationButtons = List(backButton, forwardButton, stopOrRefreshButton, homeButton)
  private val buttonResizeListener = new InvalidationListener {
    def invalidated(observable:Observable) {
      val maxSideSize = navigationButtons.flatMap(button => {
        List(button.prefWidth(Integer.MAX_VALUE), button.prefHeight(Integer.MAX_VALUE))
      }).max
      navigationButtons.foreach(button => {
        button.setPrefWidth(maxSideSize)
        button.setPrefHeight(maxSideSize)
      })
    }
  }
  navigationButtons.foreach(button => {
    button.widthProperty.addListener(buttonResizeListener)
    button.heightProperty.addListener(buttonResizeListener)
  })
  private val addressBar = new AddressBar(browser.pageLongText)
  HBox.setHgrow(addressBar, Priority.ALWAYS)
  private val settingsButton = new SettingsMenuButton(tabPane, stage)

  getItems.addAll(navigationButtons.toArray :_*)
  getItems.addAll(addressBar, settingsButton)
}

class SettingsMenuButton(tabPane:BrowserTabPane, stage:BrowserStage) extends MenuButton {
  setGraphic(new FontAwesomeLabel(Cog))

  private val newTabMenuItem = new MenuItem("New Tab")
  newTabMenuItem.setAccelerator(keyMap.newTab.accelerator)
  newTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(HomePage)}})

  private val newWindowMenuItem = new MenuItem("New Window")
  newWindowMenuItem.setAccelerator(keyMap.newWindow.accelerator)
  newWindowMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {stage.createStage(HomePage)}})

  private val utilsMenuItem = new MenuItem("Utils")
  utilsMenuItem.setAccelerator(keyMap.utilsPage.accelerator)
  utilsMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(UtilsPage)}})

  getItems.addAll(newTabMenuItem, newWindowMenuItem, separatorMenuItem, utilsMenuItem)
}

class AddressBar(text:StringBinding) extends TextField {
  textProperty.bind(text)
}

class ToolBarButton(fontAwesome:FontAwesome, disabled:BooleanBinding) extends Button {
  getStyleClass.add("tool-bar-button")
  private val iconLabel = new FontAwesomeLabel(fontAwesome)

  setGraphic(iconLabel)
  setFocusTraversable(false)
  disableProperty.bind(disabled)
  def updateIcon(fontAwesome:FontAwesome) {iconLabel.setText(fontAwesome.text)}
}

class SingleActionToolBarButton(fontAwesome:FontAwesome, action: ()=>Unit, disabled:BooleanBinding) extends ToolBarButton(fontAwesome, disabled) {
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action()}})
}

class StopOrRefreshToolBarButton(fontAwesome:FontAwesome, action: (Boolean)=>Unit, disabled:BooleanBinding,
                                 working:SimpleBooleanProperty) extends ToolBarButton(fontAwesome, disabled) {
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action(working.get)}})
  working.addListener(new ChangeListener[JBoolean] {
    def changed(observable:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
      if (newValue) updateIcon(Remove) else updateIcon(Refresh)
    }
  })
}