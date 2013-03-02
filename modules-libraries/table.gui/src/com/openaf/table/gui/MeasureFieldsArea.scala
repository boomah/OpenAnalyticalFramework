package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label
import com.openaf.table.api.TableData
import javafx.beans.property.SimpleObjectProperty

class MeasureFieldsArea(tableData:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends StackPane with DropTargetContainer with DropTarget {
  private val descriptionLabel = new Label("Drop Measure and Column Fields Here")
  getChildren.add(descriptionLabel)

  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = List(this)
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = tableData
  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {}
  def removeDropTargets() {}
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData
  }
}
