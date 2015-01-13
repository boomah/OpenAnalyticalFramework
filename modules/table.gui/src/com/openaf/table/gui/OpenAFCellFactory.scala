package com.openaf.table.gui

import javafx.util.Callback
import com.openaf.table.gui.OpenAFTableView._

trait OpenAFCellFactory extends Callback[TableColumnType,OpenAFTableCell] {
  var shouldUpdateItem = true
}
