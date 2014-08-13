package com.openaf.table.gui

import javafx.scene.control.ToggleButton
import com.openaf.gui.utils.{FontAwesome, FontAwesomeText}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import javafx.beans.property.Property
import com.openaf.table.lib.api.TableData
import javafx.stage.Popup
import java.util.Locale
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.{Boolean => JBoolean}

class FilterButton(tableData:Property[TableData], locale:Property[Locale]) extends ToggleButton {
  getStyleClass.add("filter-button")
  setFocusTraversable(false)
  setGraphic(new FontAwesomeText(FontAwesome.Filter))
  private lazy val filterButtonNodePopup = {
    val popup = new Popup
    popup.setAutoFix(true)
    popup.setAutoHide(true)
    popup.setHideOnEscape(true)
    popup.getContent.add(new FilterButtonNode(tableData, locale, cancel))
    popup.showingProperty.addListener(new ChangeListener[JBoolean] {
      def changed(observableValue:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
        if (!newValue) {setSelected(false)}
      }
    })
    popup
  }
  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(e:MouseEvent) {
      val bounds = getParent.localToScreen(getParent.getBoundsInLocal)
      filterButtonNodePopup.show(getScene.getWindow, bounds.getMinX, bounds.getMaxY)
    }
  })
  private def cancel() {filterButtonNodePopup.hide()}
}
