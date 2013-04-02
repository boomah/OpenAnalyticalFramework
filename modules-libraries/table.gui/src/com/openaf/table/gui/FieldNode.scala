package com.openaf.table.gui

import javafx.scene.control.Label
import com.openaf.table.api.{TableData, Field}
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.HBox

class FieldNode(val field:Field, val dragAndDrop:DragAndDrop, val draggableParent:DraggableParent,
                val tableData:SimpleObjectProperty[TableData]) extends HBox with Draggable {
  getStyleClass.add("field-node")
  def fields = List(field)
  private val nameLabel = new Label(field.displayName)
  getChildren.add(nameLabel)
}