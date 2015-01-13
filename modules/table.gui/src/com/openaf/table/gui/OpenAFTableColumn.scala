package com.openaf.table.gui

import com.openaf.table.gui.OpenAFTableView._
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import com.openaf.table.lib.api.OpenAFTableRow
import javafx.beans.value.ObservableValue
import javafx.beans.property.ReadOnlyObjectWrapper

class OpenAFTableColumn extends TableColumnType {
  // This is a hack to stop a user from dragging columns about using the column headers. There should be a proper api
  // for this in future versions of JavaFX
  impl_setReorderable(false)
  setSortable(false)
  setCellValueFactory(new CellValueFactory)

  var columnIndex = 0
}

class CellValueFactory extends Callback[CellDataFeatures[OpenAFTableRow,OpenAFTableRow],ObservableValue[OpenAFTableRow]] {
  def call(cellDataFeatures:CellDataFeatures[OpenAFTableRow,OpenAFTableRow]) = {
    new ReadOnlyObjectWrapper[OpenAFTableRow](cellDataFeatures.getValue)
  }
}
