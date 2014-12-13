package com.openaf.table.gui

import javafx.scene.control.TableCell
import com.openaf.gui.utils.GuiUtils

object TableCellStyle extends Enumeration {
  type TableCellStyle = Value

  val StandardRowHeaderTableCell, RightRowHeaderTableCell, BottomRowHeaderTableCell, BottomRightRowHeaderTableCell = Value
  val FieldRowHeaderTableCell, RightFieldRowHeaderTableCell = Value
  val TotalRowHeaderTableCell = Value

  val StandardColumnHeaderTableCell, RightColumnHeaderTableCell, BottomColumnHeaderTableCell, BottomRightColumnHeaderTableCell = Value
  val TotalColumnHeaderTableCell = Value
  val FieldColumnHeaderTableCell, RightFieldColumnHeaderTableCell, BottomFieldColumnHeaderTableCell, BottomRightFieldColumnHeaderTableCell = Value

  val RightDataTableCell, BottomDataTableCell, BottomRightDataTableCell = Value
  val RowTotalDataTableCell, ColumnTotalDataTableCell = Value

  def removeAllStyles(tableCell:TableCell[_,_]) {
    val styles = values.map(value => GuiUtils.camelCaseToDashed(value.toString)).toArray
    tableCell.setId(null)
    tableCell.getStyleClass.removeAll(styles:_*)
  }
}
