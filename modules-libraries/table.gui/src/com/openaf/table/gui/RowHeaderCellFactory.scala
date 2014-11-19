package com.openaf.table.gui

import com.openaf.table.lib.api.{TableValues, OpenAFTableRow, FieldID, Renderer}
import TableValues._
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding
import javafx.util.Callback
import annotation.tailrec
import com.openaf.table.gui.OpenAFTableView.{TableColumnType, OpenAFTableCell}
import TableCellStyle._
import com.openaf.gui.utils.GuiUtils._
import com.openaf.table.gui.binding.TableLocaleStringBinding
import javafx.beans.property.Property
import java.util.Locale

class RowHeaderCellFactory[T](values:Array[Any], renderer:Renderer[T], fieldBindings:ObservableMap[FieldID,StringBinding],
                              startRowHeaderValuesIndex:Int,
                              locale:Property[Locale]) extends Callback[TableColumnType,OpenAFTableCell] {
  def call(tableColumn:TableColumnType) = new OpenAFTableCell {
    private val rowHeaderTableColumn = tableColumn.asInstanceOf[OpenAFTableColumn]
    private def addStyle(style:TableCellStyle) {getStyleClass.add(camelCaseToDashed(style.toString))}
    override def updateItem(row:OpenAFTableRow, isEmpty:Boolean) {
      super.updateItem(row, isEmpty)
      removeAllStyles(this)
      textProperty.unbind()
      if (isEmpty) {
        setText(null)
      } else {
        addStyle(StandardRowHeaderTableCell)
        val rightTableCell = rowHeaderTableColumn.column == (row.rowHeaderValues.length - 1)

        def addStyleBasedOnTableCellPosition() {
          if (row.row == (getTableView.getItems.size - 1)) {
            val style = if (rightTableCell) BottomRightRowHeaderTableCell else BottomRowHeaderTableCell
            addStyle(style)
          } else if (rightTableCell) {
            addStyle(RightRowHeaderTableCell)
          }
        }

        val intValue = row.rowHeaderValues(rowHeaderTableColumn.column)
        if (intValue == NoValueInt) {
          if (rightTableCell) {addStyle(RightRowHeaderTableCell)}
          setText(null)
        } else if (intValue == FieldInt) {
          if (rightTableCell) {
            addStyle(RightFieldRowHeaderTableCell)
          } else {
            addStyle(FieldRowHeaderTableCell)
          }
          val fieldID = values(FieldInt).asInstanceOf[FieldID]
          Option(fieldBindings.get(fieldID)) match {
            case Some(binding) => textProperty.bind(binding)
            case None => setText(fieldID.id)
          }
        } else if (intValue == TotalTopInt || intValue == TotalBottomInt) {
          addStyleBasedOnTableCellPosition()
          addStyle(TotalRowHeaderTableCell)
          val shouldRender = (rowHeaderTableColumn.column == 0) || (row.rowHeaderValues(rowHeaderTableColumn.column - 1) != intValue)
          if (shouldRender) textProperty.bind(new TableLocaleStringBinding("total", locale)) else setText(null)
        } else {
          addStyleBasedOnTableCellPosition()
          val shouldRender = {
            if (row.row == startRowHeaderValuesIndex) {
              true // Always render the top row header value
            } else {
              if (getTableColumn.getCellData(row.row - 1).rowHeaderValues(rowHeaderTableColumn.column) != intValue) {
                true // The value in the row above is different so render this value
              } else {
                // Check the values in the previous column to see if we need to render. This is so if the values are the
                // same in this column but split by the previous column we should render
                shouldRenderDueToLeftColumn(row.row, rowHeaderTableColumn.column)
              }
            }
          }
          if (shouldRender) {
            val value = values(intValue).asInstanceOf[T]
            setText(renderer.render(value))
          } else {
            setText(null)
          }
        }
      }
    }

    @tailrec private def shouldRenderDueToLeftColumn(rowIndex:Int, columnIndex:Int):Boolean = {
      if (columnIndex == 0) {
        false
      } else {
        val valueToTheLeft = getTableColumn.getCellData(rowIndex).rowHeaderValues(columnIndex - 1)
        val valueAboveToTheLeft = getTableColumn.getCellData(rowIndex - 1).rowHeaderValues(columnIndex - 1)
        if (valueToTheLeft != valueAboveToTheLeft) {
          true
        } else {
          shouldRenderDueToLeftColumn(rowIndex, columnIndex - 1)
        }
      }
    }
  }
}

