package com.openaf.table.gui

import javafx.scene.layout.FlowPane
import com.openaf.table.api.TableData

class DropTargetNode(dropTargetContainer:DropTargetContainer) extends FlowPane with DropTarget {
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    dropTargetContainer.childFieldsDropped(this, draggableFieldsInfo, tableData)
  }

  setPrefWidth(15)

  setStyle("-fx-background-color: #CCFF99AA;")
}
