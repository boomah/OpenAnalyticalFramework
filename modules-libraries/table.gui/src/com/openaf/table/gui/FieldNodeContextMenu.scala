package com.openaf.table.gui

import com.openaf.table.lib.api._
import javafx.beans.property.Property
import javafx.scene.control._
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.Boolean

class FieldNodeContextMenu[T](field:Field[T], requestTableStateProperty:Property[TableState],
                              locale:Property[Locale]) extends ContextMenu {
  private def stringBinding(id:String) = new TableLocaleStringBinding(id, locale)

  {
    val removeMenuItem = new MenuItem
    removeMenuItem.textProperty.bind(stringBinding("remove"))
    removeMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTableState = requestTableStateProperty.getValue.remove(List(field))
        requestTableStateProperty.setValue(newTableState)
      }
    })
    getItems.add(removeMenuItem)
  }
  
  if (field.fieldType.isDimension) {
    val reverseSortOrderMenuItem = new MenuItem
    reverseSortOrderMenuItem.textProperty.bind(stringBinding("reverseSortOrder"))
    reverseSortOrderMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.flipSortOrder)
        requestTableStateProperty.setValue(newTableState)
      }
    })
    getItems.addAll(new SeparatorMenuItem, reverseSortOrderMenuItem)

    val topTotalMenuItem = new CheckMenuItem
    topTotalMenuItem.textProperty.bind(stringBinding("topTotal"))
    topTotalMenuItem.selectedProperty.set(field.totals.top)
    topTotalMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
      def changed(observable:ObservableValue[_<:Boolean], oldValue:Boolean, newValue:Boolean) {
        val newTotals = field.totals.copy(top = newValue)
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(newTableState)
      }
    })

    val bottomTotalMenuItem = new CheckMenuItem
    bottomTotalMenuItem.textProperty.bind(stringBinding("bottomTotal"))
    bottomTotalMenuItem.selectedProperty.set(field.totals.bottom)
    bottomTotalMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
      def changed(observable:ObservableValue[_<:Boolean], oldValue:Boolean, newValue:Boolean) {
        val newTotals = field.totals.copy(bottom = newValue)
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(newTableState)
      }
    })
    getItems.addAll(new SeparatorMenuItem, topTotalMenuItem, bottomTotalMenuItem)

    val expandAllMenuItem = new MenuItem
    expandAllMenuItem.textProperty.bind(stringBinding("expandAll"))
    expandAllMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTotals = field.totals.copy(collapsedState = AllExpanded())
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(newTableState)
      }
    })

    val collapseAllMenuItem = new MenuItem
    collapseAllMenuItem.textProperty.bind(stringBinding("collapseAll"))
    collapseAllMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTotals = field.totals.copy(collapsedState = AllCollapsed())
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(newTableState)
      }
    })
    getItems.addAll(new SeparatorMenuItem, expandAllMenuItem, collapseAllMenuItem)
  }
}
