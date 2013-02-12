package com.openaf.table.gui

import javafx.scene.layout.HBox
import javafx.scene.control.{Toggle, ToggleGroup, ToggleButton}
import javafx.scene.{Node, Group}
import javafx.collections.{ObservableList, FXCollections}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.beans.property.Property
import com.openaf.table.api.TableData

class ConfigArea(tableDataProperty:Property[TableData], dragAndDrop:DragAndDrop) extends HBox {
  private val fieldsButton = new ToggleButton("Fields")

  private val fieldsToConfigComponent = Map[Toggle,Node](fieldsButton -> new AllFieldsArea(tableDataProperty, dragAndDrop))

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
      case Some(toggle) => updateConfigArea(fieldsToConfigComponent(toggle))
      case None => clearConfigArea()
    }
  }
  getChildren.add(buttonBar)
}

class ButtonBar(buttons:ObservableList[ToggleButton]) extends Group {
  private val bar = new HBox
  FXCollections.reverse(buttons)
  bar.getChildren.addAll(buttons)
  bar.setRotate(-90)
  getChildren.add(bar)
}