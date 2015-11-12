package com.openaf.table.gui

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.{Orientation, Pos}
import javafx.scene.control._

import com.openaf.gui.utils.Icons._
import com.openaf.table.gui.binding.TableLocaleStringBinding
import com.openaf.table.lib.api.{TableLayout, TableState}

class OpenAFTableToolBar(tableFields:OpenAFTableFields) extends ToolBar {
  getStyleClass.add("openaf-table-tool-bar")

  val clearLayoutButton = OpenAFTableToolBarButton(ClearLayout, "clearLayout", tableFields) { () =>
    val newTableState = tableFields.requestTableStateProperty.getValue.copy(tableLayout = TableLayout.Blank)
    tableFields.requestTableStateProperty.setValue(newTableState)
  }

  val topRowGrandTotalsButton = TotalsToolBarToggleButton("topRowGrandTotals", tableFields, Pos.TOP_CENTER) { () =>
    val newTableState = tableFields.requestTableStateProperty.getValue.toggleTopRowGrandTotal
    tableFields.requestTableStateProperty.setValue(newTableState)
  }
  topRowGrandTotalsButton.getStyleClass.add("top-total")

  val bottomRowGrandTotalsButton = TotalsToolBarToggleButton("bottomRowGrandTotals", tableFields, Pos.BOTTOM_CENTER) { () =>
    val newTableState = tableFields.requestTableStateProperty.getValue.toggleBottomRowGrandTotal
    tableFields.requestTableStateProperty.setValue(newTableState)
  }
  bottomRowGrandTotalsButton.getStyleClass.add("bottom-total")

  tableFields.requestTableStateProperty.addListener(new ChangeListener[TableState] {
    def changed(observable:ObservableValue[_<:TableState], oldTableState:TableState, newTableState:TableState) {
      topRowGrandTotalsButton.setSelected(newTableState.rowGrandTotals.top)
      bottomRowGrandTotalsButton.setSelected(newTableState.rowGrandTotals.bottom)
    }
  })

  getItems.addAll(topRowGrandTotalsButton, bottomRowGrandTotalsButton, new Separator, clearLayoutButton)
}

class OpenAFTableToolBarButton(val iconCode:String, val tooltipCode:String, val tableFields:OpenAFTableFields,
                               val action:() => Unit) extends Button with OpenAFTableToolBarButtonBase

object OpenAFTableToolBarButton {
  def apply(iconCode:String, tooltipCode:String, tableFields:OpenAFTableFields)(action:() => Unit) = {
    new OpenAFTableToolBarButton(iconCode, tooltipCode, tableFields, action)
  }
}

class OpenAFTableToolBarToggleButton(val iconCode:String, val tooltipCode:String, val tableFields:OpenAFTableFields,
                                     val action:() => Unit) extends ToggleButton with OpenAFTableToolBarButtonBase

object OpenAFTableToolBarToggleButton {
  def apply(iconCode:String, tooltipCode:String, tableFields:OpenAFTableFields)(action:() => Unit) = {
    new OpenAFTableToolBarToggleButton(iconCode, tooltipCode, tableFields, action)
  }
}

trait OpenAFTableToolBarButtonBase extends ButtonBase {
  def iconCode:String
  def tooltipCode:String
  def tableFields:OpenAFTableFields
  def action:() => Unit

  getStyleClass.add("openaf-table-tool-bar-button")
  setGraphic(text(iconCode))
  setFocusTraversable(false)
  setTooltip(new Tooltip)
  getTooltip.textProperty.bind(new TableLocaleStringBinding(tooltipCode, tableFields.localeProperty))
  setOnAction(new EventHandler[ActionEvent] {def handle(event:ActionEvent) {action()}})
}

class TotalsToolBarToggleButton(tooltipCode:String, tableFields:OpenAFTableFields, pos:Pos, action:() => Unit)
  extends OpenAFTableToolBarToggleButton(Sigma, tooltipCode, tableFields, action)

object TotalsToolBarToggleButton {
  def apply(tooltipCode:String, tableFields:OpenAFTableFields, pos:Pos)(action:() => Unit) = {
    new TotalsToolBarToggleButton(tooltipCode, tableFields, pos, action)
  }
}
