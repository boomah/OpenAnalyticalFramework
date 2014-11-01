package com.openaf.table.gui

import javafx.scene.control._
import javafx.beans.property.{ReadOnlyObjectWrapper, Property}
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api._
import javafx.collections.{ObservableMap, FXCollections}
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import scala.annotation.tailrec
import javafx.beans.binding.StringBinding
import java.util

object OpenAFTableView {
  type TableColumnType = TableColumn[OpenAFTableRow,OpenAFTableRow]
  type OpenAFTableCell = TableCell[OpenAFTableRow,OpenAFTableRow]
}
import OpenAFTableView._

class OpenAFTableView(tableDataProperty:Property[TableData],
                      fieldBindings:ObservableMap[FieldID,StringBinding]) extends TableView[OpenAFTableRow] {
  getStyleClass.add("openaf-table-view")
  getSelectionModel.setCellSelectionEnabled(true)
  getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

  // I have to override the skin because the default one adds an additional 10 pixels when figuring out how width each 
  // column should be. This removes that padding. I have raised an issue so hopefully in some future JavaFX release this
  // can be removed.
  override def createDefaultSkin() = {
    new com.sun.javafx.scene.control.skin.TableViewSkin[OpenAFTableRow](this) {
      override def resizeColumnToFitContent(tableColumn:TableColumn[OpenAFTableRow,_], maxRows:Int) {
        super.resizeColumnToFitContent(tableColumn, maxRows)
        tableColumn.impl_setWidth(tableColumn.getWidth - 10)
      }
    }
  }

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      setUpTableView(Option(oldTableData), newTableData)
    }
  })

  private def setUpTableView(oldTableDataOption:Option[TableData], newTableData:TableData) {
    if (newTableData.rowHeaderFields.isEmpty && newTableData.columnHeaderLayout.isEmpty) {
      getColumns.clear()
      setItems(null)
    } else {
      // TODO - should update the columns rather than recreating them
      fullSetup(newTableData)
    }
  }
  
  private def fullSetup(tableData:TableData) {
    val columnNames = new ExcelColumnNameIterator
    val columns = createRowHeaderTableColumns(tableData, columnNames)
    columns.addAll(createColumnHeaderTableColumns(tableData, columnNames))
    getColumns.clear()
    getColumns.addAll(columns)

    setItems(FXCollections.observableArrayList[OpenAFTableRow](tableData.tableValues.rows:_*))
  }

  // This is a hack to stop a user from dragging columns about using the column headers. There should be a proper api
  // for this in future versions of JavaFX
  private def disableReordering(tableColumn:TableColumn[_,_]) {tableColumn.impl_setReorderable(false)}

  private def createRowHeaderTableColumns(tableData:TableData, columnNames:Iterator[String]) = {
    val rowHeaderFields = tableData.rowHeaderFields.toArray
    val numRowHeaderFields = rowHeaderFields.length
    val columns = new util.ArrayList[TableColumnType](tableData.rowHeaderFields.length)
    val valueLookUp = tableData.tableValues.valueLookUp
    var field:Field[_] = null
    var tableColumn:OpenAFTableColumn = null
    var rowHeaderFieldCounter = 0
    val paths = tableData.columnHeaderLayout.paths
    val startRowHeaderValuesIndex = if (paths.isEmpty) 1 else paths.map(_.fields.size).max
    while (rowHeaderFieldCounter < numRowHeaderFields) {
      field = rowHeaderFields(rowHeaderFieldCounter)
      tableColumn = new OpenAFTableColumn(rowHeaderFieldCounter)
      tableColumn.setText(columnNames.next)
      disableReordering(tableColumn)
      tableColumn.setSortable(false)
      tableColumn.setCellValueFactory(new CellValueFactory)
      tableColumn.setCellFactory(new RowHeaderCellFactory(valueLookUp(field.id), tableData.defaultRenderers(field.id),
        fieldBindings, startRowHeaderValuesIndex))
      columns.add(tableColumn)

      rowHeaderFieldCounter += 1
    }
    columns
  }

  private def createColumnHeaderTableColumns(tableData:TableData, columnNames:Iterator[String]) = {
    val numColumns = tableData.tableValues.columnsPerPath.sum
    val columns = new util.ArrayList[TableColumnType](numColumns)
    val paths = tableData.columnHeaderLayout.paths
    val maxPathLength = if (paths.isEmpty) 0 else paths.map(_.fields.size).max
    val numPaths = paths.length
    var path:ColumnHeaderLayoutPath = null
    val valueLookUp = tableData.tableValues.valueLookUp
    val allPathValueLookUps = paths.map(path => path.fields.map(field => valueLookUp(field.id)).toArray).toArray
    var valueLookUps:Array[Array[Any]] = null
    val allPathRenderers:Array[Array[Renderer[_]]] = paths.map(path => path.fields.map(field => {
      tableData.defaultRenderers(field.id)
    }).toArray).toArray
    var pathRenderers:Array[Renderer[_]] = null
    var pathCounter = 0
    var numColumnsPerPath = 0
    var columnPathCounter = 0
    var tableColumn:OpenAFTableColumn = null
    var columnCounter = 0
    val pathBoundaries = tableData.tableValues.columnsPerPath.scanLeft(0)(_ + _).toSet

    while (pathCounter < numPaths) {
      path = paths(pathCounter)
      pathRenderers = allPathRenderers(pathCounter)
      numColumnsPerPath = tableData.tableValues.columnsPerPath(pathCounter)
      valueLookUps = allPathValueLookUps(pathCounter)

      while (columnPathCounter < numColumnsPerPath) {
        tableColumn = new OpenAFTableColumn(columnCounter)
        tableColumn.setText(columnNames.next.toString)
        disableReordering(tableColumn)
        tableColumn.setSortable(false)

        tableColumn.setCellValueFactory(new CellValueFactory)
        tableColumn.setCellFactory(
          new ColumnHeaderAndDataCellFactory(valueLookUps, fieldBindings, path, maxPathLength, pathRenderers, pathBoundaries)
        )
        columns.add(tableColumn)

        columnCounter += 1
        columnPathCounter += 1
      }

      columnPathCounter = 0
      pathCounter += 1
    }

    columns
  }
}

class CellValueFactory extends Callback[CellDataFeatures[OpenAFTableRow,OpenAFTableRow],ObservableValue[OpenAFTableRow]] {
  def call(cellDataFeatures:CellDataFeatures[OpenAFTableRow,OpenAFTableRow]) = {
    new ReadOnlyObjectWrapper[OpenAFTableRow](cellDataFeatures.getValue)
  }
}

class RowHeaderCellFactory[T](values:Array[Any], renderer:Renderer[T], fieldBindings:ObservableMap[FieldID,StringBinding],
                              startRowHeaderValuesIndex:Int) extends Callback[TableColumnType,OpenAFTableCell] {

  def call(tableColumn:TableColumnType) = new OpenAFTableCell {
    private val rowHeaderTableColumn = tableColumn.asInstanceOf[OpenAFTableColumn]
    override def updateItem(row:OpenAFTableRow, isEmpty:Boolean) {
      super.updateItem(row, isEmpty)
      textProperty.unbind()
      if (isEmpty) {
        setText(null)
      } else {
        val intValue = row.rowHeaderValues(rowHeaderTableColumn.column)
        if (intValue == TableValues.NoValueInt) {
          setText(null)
        } else if (intValue == TableValues.FieldInt) {
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

class ColumnHeaderAndDataCellFactory(valueLookUps:Array[Array[Any]], fieldBindings:ObservableMap[FieldID,StringBinding],
                                     path:ColumnHeaderLayoutPath, maxPathLength:Int, pathRenderers:Array[Renderer[_]],
                                     pathBoundaries:Set[Int]) extends Callback[TableColumnType,OpenAFTableCell] {
  def call(tableColumn:TableColumnType) = new OpenAFTableCell {
    private val columnHeaderTableColumn = tableColumn.asInstanceOf[OpenAFTableColumn]
    override def updateItem(row:OpenAFTableRow, isEmpty:Boolean) {
      super.updateItem(row, isEmpty)
      textProperty.unbind()
      setId(null)
      if (isEmpty) {
        setText(null)
      } else {
        if (row.row < maxPathLength) {
          row.columnHeaderAndDataValues(columnHeaderTableColumn.column) match {
            case TableValues.FieldInt => {
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
            case intValue:Int if intValue != TableValues.NoValueInt => {
              if (shouldRender(row, intValue)) {
                val value = valueLookUps(row.row)(intValue)
                val renderer = pathRenderers(row.row).asInstanceOf[Renderer[Any]]
                setText(renderer.render(value))
              } else {
                setText(null)
              }
            }
            case other => setText(null)
          }
        } else {
          path.measureFieldOption.foreach(measureField => {
            setId(s"table-cell-${measureField.id.id}")
          })
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

class OpenAFTableColumn(val column:Int) extends TableColumnType