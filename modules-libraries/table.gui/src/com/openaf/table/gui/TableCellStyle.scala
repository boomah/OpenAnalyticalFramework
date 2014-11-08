package com.openaf.table.gui

import javafx.scene.control.TableCell

object TableCellStyle {
  val StandardRowHeader = "standard-row-header-table-cell"
  val RightRowHeader = "right-row-header-table-cell"
  val FieldRowHeader = "field-row-header-table-cell"

  val StandardColumnHeader = "standard-column-header-table-cell"
  val FieldColumnHeader = "field-column-header-table-cell"
  val BottomColumnHeader = "bottom-column-header-table-cell"

  def removeAllStyles(tableCell:TableCell[_,_]) {
    tableCell.getStyleClass.removeAll(StandardRowHeader, RightRowHeader, FieldRowHeader, FieldColumnHeader,
      BottomColumnHeader)
    tableCell.setId(null)
  }
}
