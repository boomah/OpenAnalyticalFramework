package com.openaf.table.gui

import javafx.scene.control.Label
import com.openaf.table.api.{TableData, Field}
import javafx.beans.property.SimpleObjectProperty

class FieldNode(field:Field, val dragAndDrop:DragAndDrop, val draggableParent:DraggableParent,
                val tableData:SimpleObjectProperty[TableData]) extends Label(field.displayName) with Draggable {
  def fields = List(field)
}