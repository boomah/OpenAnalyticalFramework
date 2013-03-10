package com.openaf.table.gui

import javafx.scene.layout.StackPane
import com.openaf.table.api.TableData
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}
import javafx.scene.control.Label

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop)
  extends StackPane with DragAndDropNode with DropTarget with DropTargetContainer {
  getStyleClass.add("measure-fields-area")
  def description = new SimpleStringProperty("Drop Measure and Column Fields Here")
  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = List(this)
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = tableData
  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {}
  def removeDropTargets() {}
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData
  }
  private val descriptionLabel = new Label
  descriptionLabel.textProperty.bind(description)

  def setup() {
    getChildren.clear()
    getChildren.add(descriptionLabel)
  }
}
