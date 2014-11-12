package com.openaf.table.gui

import javafx.collections.ObservableMap
import com.openaf.table.lib.api._
import javafx.beans.binding.StringBinding
import javafx.util.Callback
import com.openaf.table.lib.api.ColumnHeaderLayoutPath
import scala.annotation.tailrec
import com.openaf.table.gui.OpenAFTableView.{OpenAFTableCell, TableColumnType}
import TableCellStyle._
import com.openaf.gui.utils.GuiUtils._

class ColumnHeaderAndDataCellFactory(valueLookUps:Array[Array[Any]], fieldBindings:ObservableMap[FieldID,StringBinding],
                                     path:ColumnHeaderLayoutPath, maxPathLength:Int, pathRenderers:Array[Renderer[_]],
                                     pathBoundaries:Set[Int]) extends Callback[TableColumnType,OpenAFTableCell] {
  private val rightPathBoundaries = pathBoundaries.map(_ - 1).filter(_ >= 0)
  def call(tableColumn:TableColumnType) = new OpenAFTableCell {
    private val columnHeaderTableColumn = tableColumn.asInstanceOf[OpenAFTableColumn]
    private def addStyle(style:TableCellStyle) {getStyleClass.add(camelCaseToDashed(style.toString))}
    override def updateItem(row:OpenAFTableRow, isEmpty:Boolean) {
      super.updateItem(row, isEmpty)
      removeAllStyles(this)
      textProperty.unbind()
      if (isEmpty) {
        setText(null)
      } else {
        val rightBoundaryCell = rightPathBoundaries.contains(columnHeaderTableColumn.column)
        if (row.row < maxPathLength) {
          addStyle(StandardColumnHeaderTableCell)
          row.columnHeaderAndDataValues(columnHeaderTableColumn.column) match {
            case TableValues.FieldInt => {
              if (row.row == (maxPathLength - 1)) {
                val style = if (rightBoundaryCell) BottomRightFieldColumnHeaderTableCell else BottomFieldColumnHeaderTableCell
                addStyle(style)
              } else if (rightBoundaryCell) {
                addStyle(RightFieldColumnHeaderTableCell)
              } else {
                addStyle(FieldColumnHeaderTableCell)
              }
              if (shouldRender(row, TableValues.FieldInt)) {
                val fieldID = valueLookUps(row.row)(TableValues.FieldInt).asInstanceOf[FieldID]
                Option(fieldBindings.get(fieldID)) match {
                  case Some(binding) => textProperty.bind(binding)
                  case None => setText(fieldID.id)
                }
              } else {
                setText(null)
              }
            }
            case intValue:Int => {
              if (row.row == (maxPathLength - 1)) {
                val style = if (rightBoundaryCell) BottomRightColumnHeaderTableCell else BottomColumnHeaderTableCell
                addStyle(style)
              } else if (rightBoundaryCell) {
                addStyle(RightColumnHeaderTableCell)
              }
              if (intValue != TableValues.NoValueInt) {
                if (shouldRender(row, intValue)) {
                  val value = valueLookUps(row.row)(intValue)
                  val renderer = pathRenderers(row.row).asInstanceOf[Renderer[Any]]
                  setText(renderer.render(value))
                } else {
                  setText(null)
                }
              } else {
                setText(null)
              }
            }
          }
        } else {
          path.measureFieldOption.foreach(measureField => {
            setId(s"table-cell-${measureField.id.id}")
          })
          if (row.row == (getTableView.getItems.size - 1)) {
            val style = if (rightBoundaryCell) BottomRightDataTableCell else BottomDataTableCell
            addStyle(style)
          } else if (rightBoundaryCell) {
            addStyle(RightDataTableCell)
          }
          val measureFieldIndex = path.measureFieldIndex
          val renderer = if (measureFieldIndex == -1) BlankRenderer else pathRenderers(measureFieldIndex).asInstanceOf[Renderer[Any]]
          setText(renderer.render(row.columnHeaderAndDataValues(columnHeaderTableColumn.column)))
        }
      }
    }

    @tailrec private def shouldRenderDueToRowAbove(rowIndex:Int, columnIndex:Int):Boolean = {
      if (rowIndex == 0) {
        false
      } else {
        val rowAbove = getTableColumn.getCellData(rowIndex - 1)
        val valueAbove = rowAbove.columnHeaderAndDataValues(columnIndex)
        val valueAboveToTheLeft = rowAbove.columnHeaderAndDataValues(columnIndex - 1)
        if (valueAbove != valueAboveToTheLeft) {
          true
        } else {
          shouldRenderDueToRowAbove(rowIndex - 1, columnIndex)
        }
      }
    }

    private def shouldRender(row:OpenAFTableRow, intValue:Int) = {
      if (pathBoundaries.contains(columnHeaderTableColumn.column)) {
        true // Always render the first column header value per path
      } else if (row.columnHeaderAndDataValues(columnHeaderTableColumn.column - 1).asInstanceOf[Int] != intValue) {
        true // The value in the column to the left is different so render this
      } else {
        shouldRenderDueToRowAbove(row.row, columnHeaderTableColumn.column)
      }
    }
  }
}

