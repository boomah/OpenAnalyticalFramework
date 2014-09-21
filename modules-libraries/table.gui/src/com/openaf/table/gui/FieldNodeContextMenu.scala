package com.openaf.table.gui

import com.openaf.table.lib.api.{TableState, Field}
import javafx.beans.property.Property
import javafx.scene.control.{MenuItem, ContextMenu}
import java.util.Locale
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.event.{ActionEvent, EventHandler}

class FieldNodeContextMenu[T](field:Field[T], requestTableStateProperty:Property[TableState],
                              locale:Property[Locale]) extends ContextMenu {
  private def stringBinding(id:String) = new TableLocaleStringBinding(id, locale)

  private val removeMenuItem = {
    val menuItem = new MenuItem
    menuItem.textProperty.bind(stringBinding("remove"))
    menuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTableState = requestTableStateProperty.getValue.remove(List(field))
        requestTableStateProperty.setValue(newTableState)
      }
    })
    menuItem
  }

  getItems.add(removeMenuItem)
}
