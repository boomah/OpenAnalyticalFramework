package com.openaf.browser.gui

import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._
import javafx.scene.layout.{StackPane, Priority, HBox}
import javafx.beans.binding.{StringBinding, BooleanBinding}
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}
import com.openaf.gui.utils.{GuiUtils, FontAwesomeText, FontAwesome},GuiUtils._,FontAwesome._
import com.openaf.browser.gui.utils.BrowserUtils._
import com.openaf.browser.gui.pages.{UtilsPage, HomePage}
import com.openaf.browser.gui.binding.ApplicationLocaleStringBinding
import com.openaf.browser.gui.api.BrowserCache
import javafx.scene.input.{ContextMenuEvent, MouseButton, MouseEvent}

class BrowserBar(browser:Browser, tabPane:BrowserTabPane, stage:BrowserStage, cache:BrowserCache) extends ToolBar {
  getStyleClass.add("browser-bar")

  private val backButton = new SingleActionToolBarButton(ArrowLeft, browser.back, browser.backDisableProperty)
  private val forwardButton = new SingleActionToolBarButton(ArrowRight, browser.forward, browser.forwardDisableProperty)
  private val stopOrRefreshButton = new StopOrRefreshToolBarButton(Remove, (isStop) => browser.stopOrRefresh(isStop),
                                                                   browser.stopOrRefreshDisableProperty, browser.working)
  private val homeButton = new SingleActionToolBarButton(Home, browser.home, browser.homeDisableProperty)

  private val settingsButton = new SettingsMenuButton(tabPane, stage, cache)
  private val navigationButtons = List(backButton, forwardButton, stopOrRefreshButton, homeButton, settingsButton)

  private val addressBar = new AddressBar(browser.descriptionBinding)
  HBox.setHgrow(addressBar, Priority.ALWAYS)


  getItems.addAll(navigationButtons.toArray.filterNot(_ == settingsButton) :_*)
  getItems.addAll(addressBar, settingsButton)

  override def layoutChildren() {
    // Because the icons on the buttons are actually from a font the buttons are all slightly different sizes. We want
    // them to all be the same size and square. This sets them to the same size as the height of the addressBar.
    val length = addressBar.prefHeight(Double.MaxValue)
    navigationButtons.foreach(_.setPrefSize(length, length))
    super.layoutChildren()
  }
}

class SettingsMenuButton(tabPane:BrowserTabPane, stage:BrowserStage, cache:BrowserCache) extends ToggleButton {
  getStyleClass.add("settings-menu-button")
  setFocusTraversable(false)
  setGraphic(new StackPane(new FontAwesomeText(Bars)))
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

  private lazy val popup = {
    val popupMenu = new ContextMenu
    popupMenu.setAutoFix(true)
    popupMenu.setAutoHide(true)
    popupMenu.setHideOnEscape(true)
    popupMenu.showingProperty.addListener(new ChangeListener[JBoolean] {
      def changed(observableValue:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
        if (!newValue) {setSelected(false)}
      }
    })
    popupMenu.getItems.addAll(newTabMenuItem, newWindowMenuItem, separatorMenuItem, utilsMenuItem)
    popupMenu
  }

  // The popup has to be added as the context menu otherwise the accelerators on the menu items won't work. Don't show
  // it when right click happens though
  setContextMenu(popup)
  addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, new EventHandler[ContextMenuEvent] {
    def handle(event:ContextMenuEvent) {
      event.consume()
    }
  })

  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(e:MouseEvent) {
      if (e.getButton == MouseButton.PRIMARY) {
        val bounds = localToScreen(getBoundsInLocal)
        popup.show(getScene.getWindow, bounds.getMinX, bounds.getMaxY)
      }
    }
  })
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