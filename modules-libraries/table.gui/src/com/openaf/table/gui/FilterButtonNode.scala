package com.openaf.table.gui

import javafx.beans.property.Property
import com.openaf.table.lib.api._
import java.util.Locale
import javafx.scene.layout.{Priority, HBox, VBox}
import javafx.scene.control._
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}
import javafx.collections.FXCollections
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.util.Callback
import com.openaf.gui.utils.{FontAwesome, FontAwesomeText}
import javafx.beans.binding.BooleanBinding
import javafx.scene.text.{TextBoundsType, Text}
import java.util.function.Predicate
import javafx.beans.value.{ObservableValue, ChangeListener}

class FilterButtonNode[T](field:Field[T], tableData:Property[TableData], locale:Property[Locale],
                          hidePopup:()=>Unit) extends VBox {
  getStyleClass.add("filter-button-node")

  private def stringBinding(id:String) = new TableLocaleStringBinding(id, locale)

  private val buttonPanel = new HBox
  buttonPanel.getStyleClass.add("button-box")
  private val okButton = new Button
  okButton.textProperty.bind(stringBinding("ok"))
  private val cancelButton = new Button
  cancelButton.textProperty.bind(stringBinding("cancel"))

  okButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {
      hidePopup()
      filterButtonNodeModel.updateTableData()
    }
  })
  cancelButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {hidePopup()}
  })

  okButton.setDefaultButton(true)
  cancelButton.setCancelButton(true)

  buttonPanel.getChildren.addAll(okButton, cancelButton)

  private val filterTextAreaTextField = new TextField

  private val filterButtonNodeModel = new FilterButtonNodeModel[T](field, tableData, locale)
  private val listView = new ListView[Int]
  listView.getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)
  listView.setOnKeyPressed(new EventHandler[KeyEvent] {
    import com.openaf.gui.utils.EnhancedKeyEvent._
    val escapeKeyEvent = keyEvent(KeyCode.ESCAPE)
    val spaceKeyEvent = keyEvent(KeyCode.SPACE)
    val enterKeyEvent = keyEvent(KeyCode.ENTER)
    val upKeyEvent = keyEvent(KeyCode.UP)
    def handle(event:KeyEvent) {
      if (escapeKeyEvent.matches(event)) {
        hidePopup()
      } else if (spaceKeyEvent.matches(event)) {
        filterButtonNodeModel.flipValues(listView.getSelectionModel.getSelectedItems)
      } else if (enterKeyEvent.matches(event)) {
        okButton.fire()
      } else if (upKeyEvent.matches(event) && (listView.getFocusModel.getFocusedItem == 0)) {
        filterTextAreaTextField.requestFocus()
        listView.getSelectionModel.clearSelection()
      }
    }
  })
  listView.setCellFactory(new Callback[ListView[Int],ListCell[Int]] {
    def call(listView:ListView[Int]) = new FilterButtonNodeListCell[T](filterButtonNodeModel)
  })
  private val observableItems = FXCollections.observableArrayList(filterButtonNodeModel.values:_*)
  observableItems.add(0, FilterButtonNodeModel.AllValue)
  def generatePredicate(text:String) = {
    new Predicate[Int] {
      def test(intValue:Int) = {
        if (intValue == FilterButtonNodeModel.AllValue) {
          true
        } else {
          filterButtonNodeModel.text(intValue).toLowerCase.contains(text.toLowerCase)
        }
      }
    }
  }
  private val filteredItems = observableItems.filtered(generatePredicate(""))
  listView.setItems(filteredItems)

  private val filterTextAreaPanel = {
    filterTextAreaTextField.setOnKeyPressed(new EventHandler[KeyEvent] {
      import com.openaf.gui.utils.EnhancedKeyEvent._
      val downKeyEvent = keyEvent(KeyCode.DOWN)
      def handle(event:KeyEvent) {
        if (downKeyEvent.matches(event)) {
          listView.getSelectionModel.clearSelection()
          listView.getSelectionModel.selectFirst()
          listView.requestFocus()
        }
      }
    })
    filterTextAreaTextField.textProperty.addListener(new ChangeListener[String] {
      def changed(value:ObservableValue[_<:String], oldString:String, newString:String) {
        filteredItems.setPredicate(generatePredicate(newString))
      }
    })

    val buttonsDisabledBinding = new BooleanBinding {
      bind(filterTextAreaTextField.textProperty)
      def computeValue = filterTextAreaTextField.getText.isEmpty
    }

    val filterSelectedButton = new Button //TODO - put in a shortcut
    filterSelectedButton.getStyleClass.add("graphic-button")
    filterSelectedButton.disableProperty.bind(buttonsDisabledBinding)
    filterSelectedButton.setGraphic(new FontAwesomeText(FontAwesome.Filter))
    val filterSelectedButtonTooltip = new Tooltip
    filterSelectedButtonTooltip.textProperty.bind(stringBinding("filterSelectedButtonTooltip")) //TODO - put in shortcut text
    filterSelectedButton.setTooltip(filterSelectedButtonTooltip)
    filterSelectedButton.setOnAction(new EventHandler[ActionEvent] {
      def handle(e:ActionEvent) {
        hidePopup()
        import scala.collection.JavaConversions._
        val valuesToRetain = filteredItems.collect{case (intValue) if intValue != 0 => filterButtonNodeModel.value(intValue)}.toSet
        val newFilter = if (valuesToRetain.isEmpty) {
          RejectAllFilter[T]()
        } else if (valuesToRetain.size == filterButtonNodeModel.values.size) {
          RetainAllFilter[T]()
        } else {
          RetainFilter[T](valuesToRetain)
        }
        filterButtonNodeModel.updateTableData(newFilter)
      }
    })

    val generateFilterButton = new Button //TODO - put in a shortcut
    generateFilterButton.getStyleClass.add("graphic-button")
    generateFilterButton.disableProperty.bind(buttonsDisabledBinding)
    val plusText = new Text("+")
    plusText.getStyleClass.add("plus-text")
    plusText.setBoundsType(TextBoundsType.VISUAL)
    generateFilterButton.setGraphic(plusText)
    val generateFilterButtonTooltip = new Tooltip
    generateFilterButtonTooltip.textProperty.bind(stringBinding("generateFilterButtonTooltip")) //TODO - put in shortcut text
    generateFilterButton.setTooltip(generateFilterButtonTooltip)
    generateFilterButton.setOnAction(new EventHandler[ActionEvent] {
      def handle(e:ActionEvent) {
        hidePopup()
        println(s"TODO - Generate filter based on current text (${filterTextAreaTextField.getText})") // TODO - actually do this
      }
    })

    val hBox = new HBox {
      override def layoutChildren() {
        // Because the icons on the buttons are actually from a font the buttons are all slightly different sizes.
        // They should be all the same size and square. This sets them to the same size as the height of the text field.
        val length = filterTextAreaTextField.prefHeight(Double.MaxValue)
        filterSelectedButton.setPrefSize(length, length)
        generateFilterButton.setPrefSize(length, length)
        super.layoutChildren()
      }
    }
    hBox.getStyleClass.add("small-spacing")
    HBox.setHgrow(filterTextAreaTextField, Priority.ALWAYS)
    hBox.getChildren.addAll(filterTextAreaTextField, filterSelectedButton, generateFilterButton)
    hBox
  }

  private[gui] def reset() {
    filterTextAreaTextField.clear()
    filterButtonNodeModel.reset()
    listView.getSelectionModel.clearSelection()
    filterTextAreaTextField.requestFocus()
  }

  getChildren.addAll(filterTextAreaPanel, listView, buttonPanel)
}