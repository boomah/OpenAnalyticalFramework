package com.openaf.table.gui

import javafx.beans.property.Property
import com.openaf.table.lib.api.{Field, TableData}
import java.util.Locale
import javafx.scene.layout.{HBox, VBox}
import javafx.scene.control.{Button, ListView}
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}
import javafx.collections.FXCollections
import javafx.scene.input.{KeyCode, KeyEvent}

class FilterButtonNode(field:Field[_], tableData:Property[TableData], locale:Property[Locale], cancel:()=>Unit) extends VBox {
  getStyleClass.add("filter-button-node")
  private val values = tableData.getValue.tableValues.fieldValues.values(field)

  private val listView = new ListView[Int]
  listView.setOnKeyPressed(new EventHandler[KeyEvent] {
    import com.openaf.gui.utils.EnhancedKeyEvent._
    val escapeKeyEvent = keyEvent(KeyCode.ESCAPE)
    val spaceKeyEvent = keyEvent(KeyCode.SPACE)
    def handle(event:KeyEvent) {
      if (escapeKeyEvent.matches(event)) {
        cancel()
      } else if (spaceKeyEvent.matches(event)) {
        println("Space Key Pressed")
      }
    }
  })
  listView.setItems(FXCollections.observableArrayList(values:_*))

  private val buttonBox = new HBox
  buttonBox.getStyleClass.add("button-box")
  private val okButton = new Button
  okButton.textProperty.bind(new TableLocaleStringBinding("ok", locale))
  private val cancelButton = new Button
  cancelButton.textProperty.bind(new TableLocaleStringBinding("cancel", locale))

  okButton.setDefaultButton(true)
  cancelButton.setCancelButton(true)

  buttonBox.getChildren.addAll(okButton, cancelButton)

  okButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {cancel()}
  })
  cancelButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {cancel()}
  })

  getChildren.addAll(listView, buttonBox)
}