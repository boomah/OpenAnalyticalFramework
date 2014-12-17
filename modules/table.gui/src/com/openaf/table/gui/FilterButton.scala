package com.openaf.table.gui

import javafx.scene.control.ToggleButton
import com.openaf.gui.utils.{Icons, GuiUtils}
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}
import javafx.beans.property.Property
import com.openaf.table.lib.api._
import javafx.stage.{WindowEvent, Popup}
import java.util.Locale
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}

class FilterButton[T](field:Field[T], tableData:Property[TableData], requestTableStateProperty:Property[TableState],
                      locale:Property[Locale]) extends ToggleButton {
  getStyleClass.add("filter-button")
  setFocusTraversable(false)
  private val filterIcon = Icons.text(Icons.Filter)
  filterIcon.getStyleClass.add("icons-" + GuiUtils.camelCaseToDashed(field.filter.getClass.getSimpleName))
  setGraphic(filterIcon)
  private lazy val filterButtonNodePopup = {
    val popup = new Popup
    popup.setAutoFix(true)
    popup.setAutoHide(true)
    popup.setHideOnEscape(true)
    val filterButtonNode = new FilterButtonNode[T](field, tableData, requestTableStateProperty, locale, hidePopup)
    popup.getContent.add(filterButtonNode)
    popup.showingProperty.addListener(new ChangeListener[JBoolean] {
      def changed(observableValue:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
        if (!newValue) {setSelected(false)}
      }
    })
    popup.setOnShowing(new EventHandler[WindowEvent] {
      def handle(event:WindowEvent) {filterButtonNode.reset()}
    })
    popup
  }
  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(e:MouseEvent) {
      if (e.getButton == MouseButton.PRIMARY) {
        val bounds = getParent.localToScreen(getParent.getBoundsInLocal)
        filterButtonNodePopup.show(getScene.getWindow, bounds.getMinX, bounds.getMaxY)
      }
    }
  })
  private def hidePopup() {filterButtonNodePopup.hide()}
}
