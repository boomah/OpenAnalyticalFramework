package com.openaf.table.gui

import com.openaf.table.lib.api.{TableState, Field}
import javafx.beans.property.Property
import javafx.scene.control.{SeparatorMenuItem, MenuItem, ContextMenu}
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}

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
    getItems.add(new SeparatorMenuItem)
    val reverseSortOrderMenuItem = new MenuItem
    reverseSortOrderMenuItem.textProperty.bind(stringBinding("reverseSortOrder"))
    reverseSortOrderMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTableState = requestTableStateProperty.getValue.replaceField(field, field.flipSortOrder)
        requestTableStateProperty.setValue(newTableState)
      }
    })
    getItems.add(reverseSortOrderMenuItem)
  }
}
