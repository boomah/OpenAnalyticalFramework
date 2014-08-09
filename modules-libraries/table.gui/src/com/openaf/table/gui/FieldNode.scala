package com.openaf.table.gui

import javafx.scene.control.{Button, Label}
import javafx.beans.property.Property
import javafx.scene.layout.HBox
import com.openaf.table.lib.api.{FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.geometry.{Insets, Pos}
import com.openaf.gui.utils.{FontAwesome, FontAwesomeText}
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

class FieldNode(val field:Field[_], val dragAndDrop:DragAndDrop, val dragAndDropContainer:DragAndDropContainer,
                val tableData:Property[TableData], fieldBindings:ObservableMap[FieldID,StringBinding])
  extends HBox with Draggable {
  getStyleClass.add("field-node")
  setFillHeight(false)
  setAlignment(Pos.CENTER)

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

  if (field.fieldType.isDimension) {
    val filterButton = new Button
    filterButton.getStyleClass.add("filter-button")
    filterButton.setFocusTraversable(false)
    filterButton.setGraphic(new FontAwesomeText(FontAwesome.Filter))
    filterButton.setOnMousePressed(new EventHandler[MouseEvent] {
      def handle(e:MouseEvent) {println("Filter button mouse pressed")}
    })

    HBox.setMargin(filterButton, new Insets(0,0,0,3))
    getChildren.add(filterButton)
  }

  def dragImage = {
    val parameters = new SnapshotParameters
    parameters.setFill(Color.TRANSPARENT)
    snapshot(parameters, null)
  }
}