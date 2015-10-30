package com.openaf.table.gui

import javafx.scene.control.Label
import javafx.scene.layout.HBox
import com.openaf.table.lib.api.Field
import javafx.scene.SnapshotParameters
import javafx.scene.paint.Color
import javafx.geometry.Pos
import javafx.event.EventHandler
import javafx.scene.input.{MouseButton, MouseEvent}

class FieldNode[T](val field:Field[T], val dragAndDropContainer:DragAndDropContainer,
                   val tableFields:OpenAFTableFields) extends HBox with Draggable {
  getStyleClass.add("field-node")
  setFillHeight(false)
  setAlignment(Pos.CENTER)

  def fields:List[Field[_]] = List(field)

  private val nameLabel = {
    val label = new Label
    field.fieldNodeState.nameOverrideOption match {
      case Some(nameOverride) => label.setText(nameOverride)
      case None =>
        Option(tableFields.fieldBindings.get(field.id)) match {
          case Some(binding) => label.textProperty.bind(binding)
          case None => label.setText(field.id.id)
        }
    }
    label
  }
  nameLabel.getStyleClass.add("name-label")
  getChildren.add(nameLabel)

  if (field.fieldType.isDimension) {
    getStyleClass.add("field-node-dimension")
    val filterButton = new FilterButton[T](field, tableFields)
    getChildren.add(filterButton)
  } else {
    getStyleClass.add("field-node-measure")
  }

  private lazy val contextMenu = new FieldNodeContextMenu[T](field, tableFields)
  addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler[MouseEvent] {
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