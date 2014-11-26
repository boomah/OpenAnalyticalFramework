package com.openaf.table.gui

import com.openaf.table.lib.api._
import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.scene.control.MenuItem
import javafx.event.{ActionEvent, EventHandler}

class ExpandAndCollapse(field:Field[_], requestTableStateProperty:Property[TableState], locale:Property[Locale]) {
  private def stringBinding(id:String) = new TableLocaleStringBinding(id, locale)

  private def requestNewState(collapsedState:CollapsedState) {
    val newTotals = field.totals.copy(collapsedState = collapsedState)
    val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
    requestTableStateProperty.setValue(newTableState)
  }

  def expandAllMenuItem = {
    val menuItem = new MenuItem
    menuItem.textProperty.bind(stringBinding("expandAll"))
    menuItem.setOnAction(new EventHandler[ActionEvent] {def handle(event:ActionEvent) {requestNewState(AllExpanded())}})
    menuItem
  }

  def collapseAllMenuItem = {
    val menuItem = new MenuItem
    menuItem.textProperty.bind(stringBinding("collapseAll"))
    menuItem.setOnAction(new EventHandler[ActionEvent] {def handle(event:ActionEvent) {requestNewState(AllCollapsed())}})
    menuItem
  }
}
