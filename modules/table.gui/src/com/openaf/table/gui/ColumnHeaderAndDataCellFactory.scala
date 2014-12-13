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
import TableValues._
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.beans.property.Property
import java.util.Locale

class ColumnHeaderAndDataCellFactory(valueLookUps:Array[Array[Any]], fieldBindings:ObservableMap[FieldID,StringBinding],
                                     fieldPathsIndexes:Array[Int], columnHeaderLayoutPaths:Array[ColumnHeaderLayoutPath],
                                     maxPathLength:Int, pathRenderers:Array[Renderer[_]],
                                     locale:Property[Locale]) extends Callback[TableColumnType,OpenAFTableCell] {

  def call(tableColumn:TableColumnType) = new OpenAFTableCell {
    private val columnHeaderTableColumn = tableColumn.asInstanceOf[OpenAFTableColumn]

    override def updateItem(row:OpenAFTableRow, isEmpty:Boolean) {
      super.updateItem(row, isEmpty)
      removeAllStyles(this)
      textProperty.unbind()
      if (isEmpty) {
        setText(null)
      } else {
        val rightBoundaryCell = (column == (row.numColumnHeaderColumns - 1)) ||
          (fieldOption(row.row, column) != fieldOption(row.row, column + 1))
        if (row.row < maxPathLength) {
          // Column Header area
          addStyle(StandardColumnHeaderTableCell)
          row.columnHeaderAndDataValues(column) match {
            case FieldInt => {
              addStyle(FieldColumnHeaderTableCell)
              if (row.row == (maxPathLength - 1)) {
                val style = if (rightBoundaryCell) BottomRightFieldColumnHeaderTableCell else BottomFieldColumnHeaderTableCell
                addStyle(style)
              } else if (rightBoundaryCell) {
                addStyle(RightFieldColumnHeaderTableCell)
              }
              if (shouldRender(row, FieldInt)) {
                useFieldText(row.row)
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

              if (intValue == NoValueInt) {
                setText(null)
                // If there are totals above in this column style this cell so that it doesn't break the green fill down
                // to the data
                if (onColumnTotalColumn) {addStyle(TotalColumnHeaderTableCell)}
              } else if (intValue == TotalTopInt || intValue == TotalBottomInt) {
                val totalAlreadyDisplayedAbove = totalDisplayedAbove(row.row, column)
                // If we are on a field row then display the field name rather than Total or blank
                if (isFieldRow(row)) {
                  addStyle(FieldColumnHeaderTableCell)
                  // Only add the field text and show a green background if a total cell is already above the field row
                  if (totalAlreadyDisplayedAbove) {
                    addStyle(TotalColumnHeaderTableCell)
                    useFieldText(row.row)
                  } else if (intValue == TotalTopInt) {
                    useFieldText(row.row)
                  } else {
                    setText(null)
                  }
                } else {
                  addStyle(TotalColumnHeaderTableCell)
                  if (totalAlreadyDisplayedAbove) {
                    setText(null)
                  } else {
                    textProperty.bind(stringBinding(fieldOption(row.row, column).map(_.totalTextID).getOrElse("total")))
                  }
                }
              } else {
                if (shouldRender(row, intValue)) {
                  val value = valueLookUps(row.row)(intValue)
                  val renderer = pathRenderers(row.row).asInstanceOf[Renderer[Any]]
                  setText(renderer.render(value))
                } else {
                  setText(null)
                }
              }
            }
          }
        } else {
          // Data area
          columnHeaderLayoutPaths(fieldPathsIndexes(column)).measureFieldOption.foreach(measureField => {
            setId(s"table-cell-${measureField.id.id}")
          })
          // Add style if this cell represents a total
          if (onRowTotalRow(row)) {
            addStyle(RowTotalDataTableCell)
          }
          if (onColumnTotalColumn) {
            addStyle(ColumnTotalDataTableCell)
          }
          // Add style for boundary cells
          if (row.row == (getTableView.getItems.size - 1)) {
            val style = if (rightBoundaryCell) BottomRightDataTableCell else BottomDataTableCell
            addStyle(style)
          } else if (rightBoundaryCell) {
            addStyle(RightDataTableCell)
          }
          val measureFieldIndex = columnHeaderLayoutPaths(fieldPathsIndexes(column)).measureFieldIndex
          val renderer = if (measureFieldIndex == -1) BlankRenderer else pathRenderers(measureFieldIndex).asInstanceOf[Renderer[Any]]
          setText(renderer.render(row.columnHeaderAndDataValues(columnHeaderTableColumn.column)))
        }
      }
    }

    private def column = columnHeaderTableColumn.column

    private def fieldOption(rowIndex:Int, columnIndex:Int) = {
      val fields = columnHeaderLayoutPaths(fieldPathsIndexes(columnIndex)).fields
      if (rowIndex < fields.length) {
        Some(fields(rowIndex))
      } else {
        None
      }
    }
    private def addStyle(style:TableCellStyle) {getStyleClass.add(camelCaseToDashed(style.toString))}

    private def otherRow(rowIndex:Int) = getTableColumn.getCellData(rowIndex)

    private def isFieldRow(row:OpenAFTableRow) = {
      columnHeaderLayoutPaths(fieldPathsIndexes(column)).measureFieldIndex == row.row
    }

    private def totalDisplayedAbove(rowIndex:Int, columnIndex:Int) = {
      (0 until rowIndex).exists(row => {
        val value = otherRow(row).columnHeaderAndDataValues(columnIndex)
        ((value == TotalTopInt) || (value == TotalBottomInt)) && !isFieldRow(otherRow(row))
      })
    }

    private def stringBinding(id:String) = new TableLocaleStringBinding(id, locale)

    private def useFieldText(rowIndex:Int) {
      val fieldID = valueLookUps(rowIndex)(FieldInt).asInstanceOf[FieldID]
      Option(fieldBindings.get(fieldID)) match {
        case Some(binding) => textProperty.bind(binding)
        case None => setText(fieldID.id)
      }
    }

    private def onRowTotalRow(row:OpenAFTableRow) = {
      row.rowHeaderValues.exists(intValue => intValue == TotalTopInt || intValue == TotalBottomInt)
    }
    
    private def onColumnTotalColumn = {
      (0 until maxPathLength).exists(rowIndex => {
        val row = otherRow(rowIndex)
        val columnValue = row.columnHeaderAndDataValues(column)
        columnValue == TotalTopInt || columnValue == TotalBottomInt
      })
    }

    @tailrec private def shouldRenderDueToRowAbove(rowIndex:Int, columnIndex:Int):Boolean = {
      if (rowIndex == 0) {
        false
      } else {
        val rowAbove = otherRow(rowIndex - 1)
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
      if ((column == 0) || (fieldOption(row.row, column) != fieldOption(row.row, column - 1))) {
        true // Always render the first column header value per path
      } else if (row.columnHeaderAndDataValues(column - 1) != intValue) {
        // The value in the column to the left is different so as long as it isn't the special case of a total value to
        // the left of a field value then it should be rendered, apart from when the total value has already been
        // rendered above
        if ((intValue == FieldInt) && (row.columnHeaderAndDataValues(column - 1) == TotalTopInt)) {
          shouldRenderDueToRowAbove(row.row, column)
        } else {
          true
        }
      } else {
        shouldRenderDueToRowAbove(row.row, column)
      }
    }
  }
}

