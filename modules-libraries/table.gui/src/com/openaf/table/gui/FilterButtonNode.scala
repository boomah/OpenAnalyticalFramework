package com.openaf.table.gui

import javafx.beans.property.Property
import com.openaf.table.lib.api._
import java.util.Locale
import javafx.scene.layout.{HBox, VBox}
import javafx.scene.control._
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}
import javafx.collections.FXCollections
import javafx.scene.input.{MouseButton, MouseEvent, KeyCode, KeyEvent}
import javafx.util.Callback

class FilterButtonNode[T](field:Field[T], tableData:Property[TableData], locale:Property[Locale], cancel:()=>Unit) extends VBox {
  getStyleClass.add("filter-button-node")

  private val buttonBox = new HBox
  buttonBox.getStyleClass.add("button-box")
  private val okButton = new Button
  okButton.textProperty.bind(new TableLocaleStringBinding("ok", locale))
  private val cancelButton = new Button
  cancelButton.textProperty.bind(new TableLocaleStringBinding("cancel", locale))

  okButton.setDefaultButton(true)
  cancelButton.setCancelButton(true)

  private val filterButtonNodeModel = new FilterButtonNodeModel[T](field, tableData, locale)
  private val listView = new ListView[Int]
  listView.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
  listView.setOnKeyPressed(new EventHandler[KeyEvent] {
    import com.openaf.gui.utils.EnhancedKeyEvent._
    val escapeKeyEvent = keyEvent(KeyCode.ESCAPE)
    val spaceKeyEvent = keyEvent(KeyCode.SPACE)
    val enterKeyEvent = keyEvent(KeyCode.ENTER)
    def handle(event:KeyEvent) {
      if (escapeKeyEvent.matches(event)) {
        cancel()
      } else if (spaceKeyEvent.matches(event)) {
        filterButtonNodeModel.flipValues(listView.getSelectionModel.getSelectedItems)
      } else if (enterKeyEvent.matches(event)) {
        okButton.fire()
      }
    }
  })
  listView.setCellFactory(new Callback[ListView[Int],ListCell[Int]] {
    def call(listView:ListView[Int]) = new FilterButtonNodeListCell[T](filterButtonNodeModel, cancel)
  })
  private val observableProperties = FXCollections.observableArrayList(filterButtonNodeModel.values:_*)
  observableProperties.add(0, 0) // 0 represents All
  listView.setItems(observableProperties)

  buttonBox.getChildren.addAll(okButton, cancelButton)

  okButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {
      cancel()
      filterButtonNodeModel.updateTableData()
    }
  })
  cancelButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {cancel()}
  })

  getChildren.addAll(listView, buttonBox)
}

class FilterButtonNodeListCell[T](filterButtonNodeModel:FilterButtonNodeModel[T], cancel:()=>Unit) extends ListCell[Int] {
  getStyleClass.add("filter-button-node-list-cell")
  private val checkBox = new CheckBox
  checkBox.setMouseTransparent(true)
  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY && !event.isShortcutDown && !event.isShiftDown &&
        !event.isControlDown && !event.isAltDown) {
        checkBox.fire()
        filterButtonNodeModel.updateAllProperty(checkBox.isSelected)
      }
    }
  })
  setOnMouseClicked(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (event.getButton == MouseButton.PRIMARY && event.getClickCount == 2) {
        cancel()
        filterButtonNodeModel.selectValues(getListView.getSelectionModel.getSelectedItems)
      }
    }
  })
  override def updateItem(intValue:Int, isEmpty:Boolean) {
    super.updateItem(intValue, isEmpty)
    setText(null)
    checkBox.selectedProperty.unbind()
    if (isEmpty) {
      setId(null)
      setGraphic(null)
    } else {
      if (intValue == 0) {
        setId("all-filter-button-node-list-cell")
      } else {
        setId(null)
      }
      checkBox.selectedProperty.bindBidirectional(filterButtonNodeModel.property(intValue))
      checkBox.setText(filterButtonNodeModel.text(intValue))
      setGraphic(checkBox)
    }
  }
}