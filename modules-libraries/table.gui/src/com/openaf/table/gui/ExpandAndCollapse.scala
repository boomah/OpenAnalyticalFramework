package com.openaf.table.gui

import com.openaf.table.lib.api._
import javafx.beans.property.Property
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.scene.control.MenuItem
import javafx.event.{ActionEvent, EventHandler}

class ExpandAndCollapse(field:Field[_], requestTableStateProperty:Property[TableState], locale:Property[Locale]) {
  private def requestNewState(collapsedState:CollapsedState) {
    val newTotals = field.totals.copy(collapsedState = collapsedState)
    val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
    requestTableStateProperty.setValue(newTableState)
  }

  private def menuItemFromId(id:String) = {
    val menuItem = new MenuItem
    menuItem.textProperty.bind(new TableLocaleStringBinding(id, locale))
    menuItem
  }

  def expandAllMenuItem = {
    val menuItem = menuItemFromId("expandAll")
    menuItem.setOnAction(new EventHandler[ActionEvent] {def handle(event:ActionEvent) {requestNewState(AllExpanded())}})
    menuItem
  }

  def collapseAllMenuItem = {
    val menuItem = menuItemFromId("collapseAll")
    menuItem.setOnAction(new EventHandler[ActionEvent] {def handle(event:ActionEvent) {requestNewState(AllCollapsed())}})
    menuItem
  }

  def expandMenuItem(path:CollapsedStatePath) = {
    val menuItem = menuItemFromId("expand")
    menuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {requestNewState(field.totals.collapsedState + path)}
    })
    menuItem
  }

  def collapseMenuItem(path:CollapsedStatePath) = {
    val menuItem = menuItemFromId("collapse")
    menuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {requestNewState(field.totals.collapsedState - path)}
    })
    menuItem
  }
}
