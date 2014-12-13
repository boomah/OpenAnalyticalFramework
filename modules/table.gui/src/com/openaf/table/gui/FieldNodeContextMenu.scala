package com.openaf.table.gui

import com.openaf.table.lib.api._
import javafx.beans.property.Property
import javafx.scene.control._
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.lang.Boolean

class FieldNodeContextMenu[T](field:Field[T], tableDataProperty:Property[TableData], requestTableStateProperty:Property[TableState],
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

    val (topTotalStringID, bottomTotalStringID) = if (tableDataProperty.getValue.tableState.isColumnHeaderField(field)) {
      ("leftTotal", "rightTotal")
    } else {
      ("topTotal", "bottomTotal")
    }

    val topTotalMenuItem = new CheckMenuItem
    topTotalMenuItem.textProperty.bind(stringBinding(topTotalStringID))
    topTotalMenuItem.selectedProperty.set(field.totals.top)
    topTotalMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
      def changed(observable:ObservableValue[_<:Boolean], oldValue:Boolean, newValue:Boolean) {
        val newTotals = field.totals.copy(top = newValue)
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(newTableState)
      }
    })

    val bottomTotalMenuItem = new CheckMenuItem
    bottomTotalMenuItem.textProperty.bind(stringBinding(bottomTotalStringID))
    bottomTotalMenuItem.selectedProperty.set(field.totals.bottom)
    bottomTotalMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
      def changed(observable:ObservableValue[_<:Boolean], oldValue:Boolean, newValue:Boolean) {
        val newTotals = field.totals.copy(bottom = newValue)
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(newTableState)
      }
    })
    getItems.addAll(new SeparatorMenuItem, topTotalMenuItem, bottomTotalMenuItem)

    val expandAndCollapse = new ExpandAndCollapse(field, requestTableStateProperty, locale)

    getItems.addAll(new SeparatorMenuItem, expandAndCollapse.expandAllMenuItem, expandAndCollapse.collapseAllMenuItem)
  }
}
