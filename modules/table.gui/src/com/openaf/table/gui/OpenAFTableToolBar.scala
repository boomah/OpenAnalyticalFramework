package com.openaf.table.gui

import javafx.scene.control.{Tooltip, Button, ToolBar}
import com.openaf.gui.utils.Icons._
import com.openaf.table.lib.api.TableLayout
import javafx.event.{ActionEvent, EventHandler}
import com.openaf.table.gui.binding.TableLocaleStringBinding

class OpenAFTableToolBar(tableFields:OpenAFTableFields) extends ToolBar {
  getStyleClass.add("openaf-table-tool-bar")

  val clearLayoutButton = new OpenAFTableToolBarButton(ClearLayout, "clearLayout", tableFields, {
    val newTableState = tableFields.tableDataProperty.getValue.tableState.copy(tableLayout = TableLayout.Blank)
    tableFields.requestTableStateProperty.setValue(newTableState)
  })

  getItems.addAll(clearLayoutButton)
}

class OpenAFTableToolBarButton(iconCode:String, tooltipCode:String, tableFields:OpenAFTableFields, action: =>Unit) extends Button {
  getStyleClass.add("openaf-table-tool-bar-button")
  setGraphic(text(iconCode))
  setFocusTraversable(false)
  setTooltip(new Tooltip)
  getTooltip.textProperty.bind(new TableLocaleStringBinding(tooltipCode, tableFields.localeProperty))
  setOnAction(new EventHandler[ActionEvent] {def handle(event:ActionEvent) {action}})
}
