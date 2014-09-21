package com.openaf.table.gui

import javafx.scene.control.Label
import javafx.beans.property.Property
import javafx.scene.layout.HBox
import com.openaf.table.lib.api.{TableState, FieldID, TableData, Field}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.geometry.{Insets, Pos}
import java.util.Locale
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}

class FieldNode[T](val field:Field[T], val dragAndDrop:DragAndDrop, val dragAndDropContainer:DragAndDropContainer,
                   tableData:Property[TableData], val requestTableStateProperty:Property[TableState],
                   fieldBindings:ObservableMap[FieldID,StringBinding], locale:Property[Locale])
  extends HBox with Draggable {
  getStyleClass.add("field-node")
  setFillHeight(false)
  setAlignment(Pos.CENTER)

  def fields:List[Field[_]] = List(field)

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
    val filterButton = new FilterButton[T](field, tableData, requestTableStateProperty, locale)
    HBox.setMargin(filterButton, new Insets(0,0,0,3))
    getChildren.add(filterButton)
  }

  private lazy val contextMenu = new FieldNodeContextMenu[T](field, requestTableStateProperty, locale)
  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.SECONDARY) {
        contextMenu.show(getScene.getWindow, event.getScreenX, event.getScreenY)
      }
    }
  })

  def dragImage = {
    val parameters = new SnapshotParameters
    parameters.setFill(Color.TRANSPARENT)
    snapshot(parameters, null)
  }
}