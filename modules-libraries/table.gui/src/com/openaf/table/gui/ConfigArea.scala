package com.openaf.table.gui

import javafx.scene.layout.{Priority, HBox}
import javafx.scene.control.{Toggle, ToggleGroup, ToggleButton}
import javafx.scene.{Node, Group}
import javafx.collections.{ObservableMap, ObservableList, FXCollections}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import com.openaf.table.lib.api.{FieldID, TableData}
import javafx.beans.binding.StringBinding

class ConfigArea(tableDataProperty:SimpleObjectProperty[TableData], dragAndDrop:DragAndDrop,
                 locale:SimpleObjectProperty[Locale], fieldBindings:ObservableMap[FieldID,StringBinding]) extends HBox {
  getStyleClass.add("config-area")
  private val configAreaEmptyText = "config-area-empty"
  setId(configAreaEmptyText)
  private val fieldsButton = new ToggleButton
  fieldsButton.textProperty.bind(new TableLocaleStringBinding("fields", locale))
  fieldsButton.setFocusTraversable(false)

  private val fieldsToConfigComponent = Map[Toggle,Node](fieldsButton -> new AllFieldsArea(tableDataProperty, dragAndDrop, fieldBindings))

  private val toggleGroup = new ToggleGroup
  private val buttons = FXCollections.observableArrayList(fieldsButton)
  toggleGroup.getToggles.addAll(buttons)
  private val buttonBar = new ButtonBar(buttons)
  toggleGroup.selectedToggleProperty.addListener(new ChangeListener[Toggle] {
    def changed(observableValue:ObservableValue[_<:Toggle], oldToggle:Toggle, newToggle:Toggle) {updateConfigArea(Option(newToggle))}
  })
  private def clearConfigArea() {getChildren.retainAll(buttonBar)}
  private def updateConfigArea(node:Node) {
    clearConfigArea()
    getChildren.add(1, node)
  }
  private def updateConfigArea(toggleOption:Option[Toggle]) {
    toggleOption match {
      case Some(toggle) => {
        setId(null)
        updateConfigArea(fieldsToConfigComponent(toggle))
      }
      case None => {
        setId(configAreaEmptyText)
        clearConfigArea()
      }
    }
  }
  getChildren.add(buttonBar)
}

class ButtonBar(buttons:ObservableList[ToggleButton]) extends Group {
  private val bar = new HBox
  bar.getStyleClass.add("button-bar")
  FXCollections.reverse(buttons)
  bar.getChildren.addAll(buttons)
  bar.setRotate(-90)
  getChildren.add(bar)
}