package com.openaf.table.gui

import javafx.beans.property.{SimpleObjectProperty, SimpleBooleanProperty}
import com.openaf.table.api.Field

class DragAndDrop {
  val dragging = new SimpleBooleanProperty(false)
  val fieldBeingDragged = new SimpleObjectProperty[Option[Field]](None)
}
