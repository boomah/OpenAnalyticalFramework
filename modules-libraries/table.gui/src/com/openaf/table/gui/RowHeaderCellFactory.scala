package com.openaf.table.gui

import com.openaf.table.lib.api.{TableValues, OpenAFTableRow, FieldID, Renderer}
import javafx.collections.ObservableMap
import javafx.beans.binding.StringBinding
import javafx.util.Callback
import annotation.tailrec
import com.openaf.table.gui.OpenAFTableView.{TableColumnType, OpenAFTableCell}
import TableCellStyle._

class RowHeaderCellFactory[T](values:Array[Any], renderer:Renderer[T], fieldBindings:ObservableMap[FieldID,StringBinding],
                              startRowHeaderValuesIndex:Int) extends Callback[TableColumnType,OpenAFTableCell] {
  def call(tableColumn:TableColumnType) = new OpenAFTableCell {
    private val rowHeaderTableColumn = tableColumn.asInstanceOf[OpenAFTableColumn]
    override def updateItem(row:OpenAFTableRow, isEmpty:Boolean) {
      super.updateItem(row, isEmpty)
      removeAllStyles(this)
      textProperty.unbind()
      if (isEmpty) {
        setText(null)
      } else {
        getStyleClass.add(StandardRowHeader)
        val intValue = row.rowHeaderValues(rowHeaderTableColumn.column)
        if (intValue == TableValues.NoValueInt) {
          setText(null)
        } else if (intValue == TableValues.FieldInt) {
          getStyleClass.add(FieldRowHeader)
          val fieldID = values(TableValues.FieldInt).asInstanceOf[FieldID]
          Option(fieldBindings.get(fieldID)) match {
            case Some(binding) => textProperty.bind(binding)
            case None => setText(fieldID.id)
          }
        } else {
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

