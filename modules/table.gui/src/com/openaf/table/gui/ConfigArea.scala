package com.openaf.table.gui

import javafx.scene.layout.HBox
import javafx.scene.control.{Toggle, ToggleGroup, ToggleButton}
import javafx.scene.Group
import javafx.collections.{ObservableMap, ObservableList, FXCollections}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import com.openaf.table.lib.api.{TableState, FieldID, TableData}
import javafx.beans.binding.StringBinding

class ConfigArea(tableDataProperty:Property[TableData], requestTableStateProperty:Property[TableState],
                 dragAndDrop:DragAndDrop, locale:Property[Locale],
                 fieldBindings:ObservableMap[FieldID,StringBinding]) extends HBox {
  getStyleClass.add("config-area")
  private val configAreaEmptyText = "config-area-empty"
  setId(configAreaEmptyText)
  private val fieldsButton = new ToggleButton
  fieldsButton.textProperty.bind(new TableLocaleStringBinding("fields", locale, Some("1:")))
  fieldsButton.setFocusTraversable(false)

  private val fieldsToConfigAreaNode = Map[Toggle,ConfigAreaNode](
    fieldsButton -> new AllFieldsArea(tableDataProperty, requestTableStateProperty, dragAndDrop, fieldBindings)
  )

  private val toggleGroup = new ToggleGroup
  private val buttons = FXCollections.observableArrayList(fieldsButton)
  toggleGroup.getToggles.addAll(buttons)
  private val buttonBar = new ButtonBar(buttons)
  toggleGroup.selectedToggleProperty.addListener(new ChangeListener[Toggle] {
    def changed(observableValue:ObservableValue[_<:Toggle], oldToggle:Toggle, newToggle:Toggle) {updateConfigArea(Option(newToggle))}
  })
  private def clearConfigArea() {getChildren.retainAll(buttonBar)}
  private def updateConfigArea(node:ConfigAreaNode) {
    clearConfigArea()
    getChildren.add(1, node)
    node.setDefaultFocus()
  }
  private def updateConfigArea(toggleOption:Option[Toggle]) {
    toggleOption match {
      case Some(toggle) => {
        setId(null)
        updateConfigArea(fieldsToConfigAreaNode(toggle))
      }
      case None => {
        setId(configAreaEmptyText)
        clearConfigArea()
      }
    }
  }
  getChildren.add(buttonBar)

  def toggleSelected(index:Int) {
    if (index > 0 && index <= toggleGroup.getToggles.size) {
      val specifiedToggle = toggleGroup.getToggles.get(index - 1)
      val selectedToggle = toggleGroup.getSelectedToggle
      if (specifiedToggle == selectedToggle) {
        val configAreaNode = fieldsToConfigAreaNode(selectedToggle)
        if (configAreaNode.isConfigAreaNodeFocused) {
          toggleGroup.selectToggle(null)
        } else {
          configAreaNode.setDefaultFocus()
        }
      } else {
        toggleGroup.selectToggle(specifiedToggle)
      }
    }
  }

  def isConfigAreaNodeFocused = {
    Option(toggleGroup.getSelectedToggle) match {
      case Some(toggle) => fieldsToConfigAreaNode(toggle).isConfigAreaNodeFocused
      case None => false
    }
  }
}

class ButtonBar(buttons:ObservableList[ToggleButton]) extends Group {
  private val bar = new HBox
  bar.getStyleClass.add("button-bar")
  FXCollections.reverse(buttons)
  bar.getChildren.addAll(buttons)
  bar.setRotate(-90)
  getChildren.add(bar)
}