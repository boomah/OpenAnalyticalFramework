package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.Label
import com.openaf.table.api.Field

class RowHeaderFieldsArea(dragAndDrop:DragAndDrop) extends StackPane with DropTargetContainer with DropTarget {
  private val descriptionLabel = new Label("Drop Row Header Fields Here")
  getChildren.add(descriptionLabel)

  def dropTargets(draggedFields:List[Field]) = List(this)
  dragAndDrop.register(this)
}
