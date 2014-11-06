package com.openaf.table.gui

import javafx.scene.control.TableCell

object TableCellStyle {
  val StandardRowHeader = "standard-row-header-table-cell"
  val FieldRowHeader = "field-row-header-table-cell"

  def removeAllStyles(tableCell:TableCell[_,_]) {
    tableCell.getStyleClass.removeAll(StandardRowHeader, FieldRowHeader)
  }
}
