package com.openaf.browser.gui

import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._
import javafx.scene.layout.{StackPane, Priority, HBox}
import javafx.beans.binding.{StringBinding, BooleanBinding}
import javafx.beans.property.SimpleBooleanProperty
import java.lang.Boolean
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}
import javafx.beans.{Observable, InvalidationListener}
import com.openaf.browser.gui.utils.{FontAwesomeText, FontAwesome}
import com.openaf.browser.gui.utils.FontAwesome._
import com.openaf.browser.gui.utils.BrowserUtils._
import com.openaf.browser.gui.pages.{UtilsPage, HomePage}
import com.openaf.browser.gui.binding.ApplicationLocaleStringBinding
import com.openaf.browser.gui.api.BrowserCache

class BrowserBar(browser:Browser, tabPane:BrowserTabPane, stage:BrowserStage, cache:BrowserCache) extends ToolBar {
  getStyleClass.add("browser-bar")

  private val backButton = new SingleActionToolBarButton(ArrowLeft, browser.back, browser.backDisableProperty)
  private val forwardButton = new SingleActionToolBarButton(ArrowRight, browser.forward, browser.forwardDisableProperty)
  private val stopOrRefreshButton = new StopOrRefreshToolBarButton(Remove, (isStop) => browser.stopOrRefresh(isStop),
                                                                   browser.stopOrRefreshDisableProperty, browser.working)
  private val homeButton = new SingleActionToolBarButton(Home, browser.home, browser.homeDisableProperty)
  private val navigationButtons = List(backButton, forwardButton, stopOrRefreshButton, homeButton)

  private val addressBar = new AddressBar(browser.descriptionBinding)
  HBox.setHgrow(addressBar, Priority.ALWAYS)
  private val settingsButton = new SettingsMenuButton(tabPane, stage, cache)

  getItems.addAll(navigationButtons.toArray :_*)
  getItems.addAll(addressBar, settingsButton)

  override def layoutChildren() {
    // Because the icons on the buttons are actually from a font the buttons are all slightly different sizes. We want
    // them to all be the same size and square. Set here we set them to the same size as the height of the addressBar.
    val length = addressBar.prefHeight(Double.MaxValue)
    navigationButtons.foreach(_.setPrefSize(length, length))
    super.layoutChildren()
  }
}

class SettingsMenuButton(tabPane:BrowserTabPane, stage:BrowserStage, cache:BrowserCache) extends MenuButton {
  setGraphic(new StackPane(new FontAwesomeText(Cog)))
  private val newTabMenuItem = new MenuItem
  newTabMenuItem.textProperty.bind(new ApplicationLocaleStringBinding("newTab", BrowserApplication, cache))
  newTabMenuItem.setAccelerator(keyMap.newTab.accelerator)
  newTabMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(HomePage)}})

  private val newWindowMenuItem = new MenuItem
  newWindowMenuItem.textProperty.bind(new ApplicationLocaleStringBinding("newWindow", BrowserApplication, cache))
  newWindowMenuItem.setAccelerator(keyMap.newWindow.accelerator)
  newWindowMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {stage.createStage(HomePage)}})

  private val utilsMenuItem = new MenuItem
  utilsMenuItem.textProperty.bind(new ApplicationLocaleStringBinding("utilsName", BrowserApplication, cache))
  utilsMenuItem.setAccelerator(keyMap.utilsPage.accelerator)
  utilsMenuItem.setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {tabPane.createTab(UtilsPage)}})

  getItems.addAll(newTabMenuItem, newWindowMenuItem, separatorMenuItem, utilsMenuItem)
}

class AddressBar(text:StringBinding) extends TextField {
  textProperty.bind(text)
}

class ToolBarButton(fontAwesome:FontAwesome, disable:BooleanBinding) extends Button {
  getStyleClass.add("tool-bar-button")
  private val fontAwesomeText = new FontAwesomeText(fontAwesome)
  private val stackPane = new StackPane(fontAwesomeText)

  setGraphic(stackPane)
  setFocusTraversable(false)
  disableProperty.bind(disable)
  def updateIcon(fontAwesome:FontAwesome) {fontAwesomeText.setText(fontAwesome.text)}
}

class SingleActionToolBarButton(fontAwesome:FontAwesome, action: ()=>Unit, disable:BooleanBinding) extends ToolBarButton(fontAwesome, disable) {
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action()}})
}

class StopOrRefreshToolBarButton(fontAwesome:FontAwesome, action: (Boolean)=>Unit, disable:BooleanBinding,
                                 working:SimpleBooleanProperty) extends ToolBarButton(fontAwesome, disable) {
  setOnAction(new EventHandler[ActionEvent] {def handle(e:ActionEvent) {action(working.get)}})
  working.addListener(new ChangeListener[JBoolean] {
    def changed(observable:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
      if (newValue) updateIcon(Remove) else updateIcon(Refresh)
    }
  })
}