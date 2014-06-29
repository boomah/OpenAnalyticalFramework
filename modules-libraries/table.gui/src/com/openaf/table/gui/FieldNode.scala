package com.openaf.table.gui

import javafx.scene.control.Label
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.layout.HBox
import com.openaf.table.lib.api.{TableStateGenerator, FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color

class FieldNode(val field:Field[_], val dragAndDrop:DragAndDrop, val dragAndDropContainer:DragAndDropContainer,
                val tableData:SimpleObjectProperty[TableData], fieldBindings:ObservableMap[FieldID,StringBinding],
                val tableStateGenerator:SimpleObjectProperty[TableStateGenerator]) extends HBox with Draggable {

  getStyleClass.add("field-node")
  def fields = List(field)
  private val nameLabel = {
    val label = new Label
    Option(fieldBindings.get(field.id)) match {
      case Some(binding) => label.textProperty.bind(binding)
      case None => label.setText(field.id.id)
    }
    label
  }
  getChildren.add(nameLabel)

  def dragImage = {
    val parameters = new SnapshotParameters
    parameters.setFill(Color.TRANSPARENT)
    snapshot(parameters, null)
  }
}