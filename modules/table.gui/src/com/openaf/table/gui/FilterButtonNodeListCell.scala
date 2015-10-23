package com.openaf.table.gui

import javafx.scene.control.{CheckBox, ListCell}
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}
import com.openaf.gui.utils.GuiUtils
import javafx.beans.property.SimpleBooleanProperty

class FilterButtonNodeListCell[T](filterButtonNodeModel:FilterButtonNodeModel[T]) extends ListCell[Int] {
  getStyleClass.add("filter-button-node-list-cell")
  private val checkBox = new CheckBox
  checkBox.setMouseTransparent(true)
  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY && !GuiUtils.isSpecialKeyDown(event)) {
        checkBox.fire()
        filterButtonNodeModel.updateAllProperty(checkBox.isSelected)
      }
    }
  })
  setOnMouseClicked(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY && event.getClickCount == 2) {
        filterButtonNodeModel.selectOneValue(getListView.getSelectionModel.getSelectedItem)
      }
    }
  })
  private var lastPropertyOption:Option[SimpleBooleanProperty] = None
  override def updateItem(intValue:Int, isEmpty:Boolean) {
    super.updateItem(intValue, isEmpty)
    setText(null)
    checkBox.textProperty.unbind()
    lastPropertyOption.foreach(lastProperty => checkBox.selectedProperty.unbindBidirectional(lastProperty))
    if (isEmpty) {
      setId(null)
      setGraphic(null)
    } else {
      if (intValue == FilterButtonNodeModel.AllValue) {
        setId("all-filter-button-node-list-cell")
      } else {
        setId(null)
      }
      val property = filterButtonNodeModel.property(intValue)
      checkBox.selectedProperty.bindBidirectional(property)
      lastPropertyOption = Some(property)
      checkBox.textProperty.bind(filterButtonNodeModel.stringProperty(intValue))
      setGraphic(checkBox)
    }
  }
}