package com.openaf.table.gui

import javafx.beans.property.{SimpleBooleanProperty, Property}
import com.openaf.table.lib.api._
import java.util.Locale
import javafx.scene.layout.{HBox, VBox}
import javafx.scene.control._
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}
import javafx.collections.FXCollections
import javafx.scene.input.{MouseEvent, KeyCode, KeyEvent}
import javafx.util.Callback
import scala.collection.mutable

class FilterButtonNode(field:Field[_], tableData:Property[TableData], locale:Property[Locale], cancel:()=>Unit) extends VBox {
  getStyleClass.add("filter-button-node")

  private val buttonBox = new HBox
  buttonBox.getStyleClass.add("button-box")
  private val okButton = new Button
  okButton.textProperty.bind(new TableLocaleStringBinding("ok", locale))
  private val cancelButton = new Button
  cancelButton.textProperty.bind(new TableLocaleStringBinding("cancel", locale))

  okButton.setDefaultButton(true)
  cancelButton.setCancelButton(true)

  private val values = tableData.getValue.tableValues.fieldValues.values(field)
  private val lookup = tableData.getValue.tableValues.valueLookUp(field.id)
  private val booleanProperties = {
    val maxValue = if (values.length == 0) 0 else values.max
    val filter = field.filter.asInstanceOf[Filter[Any]]
    val numValues = values.length
    val array = new Array[SimpleBooleanProperty](maxValue + 1)
    var i = 0
    var intValue = -1
    while (i < numValues) {
      intValue = values(i)
      array(intValue) = new SimpleBooleanProperty(filter.matches(lookup(intValue)))
      i+= 1
    }
    array
  }
  private val defaultRenderer = tableData.getValue.defaultRenderers(field)
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
        import scala.collection.JavaConversions._
        listView.getSelectionModel.getSelectedItems.foreach(intValue => {
          val booleanProperty = booleanProperties(intValue)
          booleanProperty.set(!booleanProperty.get)
        })
      } else if (enterKeyEvent.matches(event)) {
        okButton.fire()
      }
    }
  })
  listView.setCellFactory(new Callback[ListView[Int],ListCell[Int]] {
    def call(listView:ListView[Int]) = new FilterButtonNodeListCell(booleanProperties, defaultRenderer, lookup)
  })
  listView.setItems(FXCollections.observableArrayList(values:_*))

  buttonBox.getChildren.addAll(okButton, cancelButton)

  okButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {
      cancel()
      val numProperties = booleanProperties.length
      var i = 0
      var noFilter = true // TODO - this should known by looking at the All boolean property once I put it in
      var booleanProperty:SimpleBooleanProperty = null
      val filteredValues = new mutable.HashSet[Any]
      while (i < numProperties) {
        booleanProperty = booleanProperties(i)
        if (booleanProperty != null) {
          if (booleanProperty.get) {
            filteredValues += lookup(i)
          } else {
            noFilter = false
          }
        }
        i += 1
      }
      val filter = (if (noFilter) NoFilter() else SpecifiedFilter(filteredValues.toSet)).asInstanceOf[Filter[Any]]
      val newField = field.asInstanceOf[Field[Any]].withFilter(filter)
      val newTableData = tableData.getValue.replaceField(field, newField)
      tableData.setValue(newTableData)
    }
  })
  cancelButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {cancel()}
  })

  getChildren.addAll(listView, buttonBox)
}

class FilterButtonNodeListCell[T](booleanProperties:Array[SimpleBooleanProperty], renderer:Renderer[T],
                               lookup:Array[Any]) extends ListCell[Int] {
  private val checkBox = new CheckBox
  checkBox.setMouseTransparent(true)
  setOnMousePressed(new EventHandler[MouseEvent] {
    def handle(event:MouseEvent) {
      if (!event.isShortcutDown && !event.isShiftDown && !event.isControlDown && !event.isAltDown) {
        checkBox.fire()
      }
    }
  })
  override def updateItem(intValue:Int, isEmpty:Boolean) {
    super.updateItem(intValue, isEmpty)
    setText(null)
    checkBox.selectedProperty.unbind()
    if (isEmpty) {
      setGraphic(null)
    } else {
      checkBox.selectedProperty.bindBidirectional(booleanProperties(intValue))
      checkBox.setText(renderer.render(lookup(intValue).asInstanceOf[T]))
      setGraphic(checkBox)
    }
  }
}