package com.openaf.table.gui

import javafx.scene.control.{TableCell, TableColumn, TableView}
import javafx.beans.property.{ReadOnlyObjectWrapper, Property}
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api._
import javafx.collections.{ObservableMap, FXCollections}
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import javafx.beans.binding.StringBinding
import java.util

class OpenAFTableView(tableDataProperty:Property[TableData],
                      fieldBindings:ObservableMap[FieldID,StringBinding]) extends TableView[OpenAFTableRow] {
  getStyleClass.add("openaf-table-view")
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
      oldTableDataOption match {
        case None => fullSetup(newTableData)
        case Some(oldTableData) if oldTableData.rowHeaderFields.isEmpty && oldTableData.columnHeaderLayout.isEmpty => {
          fullSetup(newTableData)
        }
        case Some(oldTableData) => {
          if (oldTableData.rowHeaderFields == newTableData.rowHeaderFields &&
            oldTableData.columnHeaderLayout == newTableData.columnHeaderLayout) {
            // The columns haven't changed so just populate the data in the table
            // TODO - if the data that is used to populate the table hasn't changed, don't bother with this
            populateTable(newTableData)
          } /*else if (oldTableData.rowHeaderFields == newTableData.rowHeaderFields) {
            // Just the column header layout has changed
            val columnHeaderColumns = createColumnHeaderTableColumns(newTableData)
            getColumns.remove(newTableData.rowHeaderFields.size, getColumns.size)
            getColumns.addAll(columnHeaderColumns :_*)
            populateTable(newTableData)
          } else if (oldTableData.columnHeaderLayout == newTableData.columnHeaderLayout) {
            // Just the rows have changed
            // TODO - For now do a full setup as an index is hardcoded into each column header column making adding or
            // TODO - removing row header columns difficult.
            fullSetup(newTableData)
          }*/ else {
            fullSetup(newTableData) // The Above two branches don't work perfectly so leave it out for now. When they
                                    // do remove this branch
          }
        }
      }
    }
  }
  
  private def fullSetup(tableData:TableData) {
    val rowHeaderTableColumns = createRowHeaderTableColumns(tableData)
    val columnHeaderTableColumns = createColumnHeaderTableColumns(tableData)

    getColumns.clear()
    getColumns.addAll(rowHeaderTableColumns ::: columnHeaderTableColumns :_*)

    populateTable(tableData)
  }

  private def populateTable(tableData:TableData) {
    val rowHeaderData = tableData.tableValues.rowHeaders

    val pathData = tableData.tableValues.data
    val numberOfPaths = pathData.length
    var path = 0
    var column = 0
    var numberOfColumns = 0
    var columnData:Array[Any] = null

    val numberOfRows = if (rowHeaderData.length > 0) {
      rowHeaderData.length
    } else {
      if (pathData.length > 0) pathData(0).length else 0
    }
    var row = 0
    val tableItems = new util.ArrayList[OpenAFTableRow](numberOfRows)
    while (row < numberOfRows) {

      val tableRow = if (rowHeaderData.length > 0) {
        new OpenAFTableRow(rowHeaderData(row))
      } else {
        if (tableData.rowHeaderFields.isEmpty) {
          new OpenAFTableRow(null)
        } else {
          new OpenAFTableRow(Array.fill(tableData.rowHeaderFields.length)(0))
        }
      }

      val columnDataListBuffer = new util.ArrayList[Any] // TODO - we can set this size as we know how long it will be
      while (path < numberOfPaths) {
        columnData = pathData(path)(row)
        numberOfColumns = columnData.length
        while (column < numberOfColumns) {
          columnDataListBuffer.add(columnData(column))
          column += 1
        }
        column = 0
        path += 1
      }
      path = 0
      tableRow.columnDataListBuffer = columnDataListBuffer
      tableItems.add(tableRow)
      row += 1
    }

    setItems(FXCollections.observableArrayList[OpenAFTableRow](tableItems))
  }
    
  private def createRowHeaderTableColumns(tableData:TableData) = {
    tableData.rowHeaderFields.zipWithIndex.map{case (field,index) => {
      val tableColumn = new TableColumn[OpenAFTableRow,Int]
      Option(fieldBindings.get(field.id)) match {
        case Some(binding) => tableColumn.textProperty.bind(binding)
        case None => tableColumn.setText(field.id.id)
      }
      tableColumn.setCellValueFactory(new DefaultRowHeaderCellValueFactory)
      val values = tableData.tableValues.valueLookUp(tableData.rowHeaderFields(index).id)
      val rowHeaderField = tableData.rowHeaderFields(index)
      val defaultRenderer = tableData.defaultRenderers(rowHeaderField)
      val cellFactory = new DefaultRowHeaderCellFactory(values, defaultRenderer)
      tableColumn.setCellFactory(cellFactory)
      tableColumn
    }}
  }

  private def createColumnHeaderTableColumn(value:Any) = {
    val tableColumn = new TableColumn[OpenAFTableRow,Any]
    value match {
      case fieldID:FieldID => {
        Option(fieldBindings.get(fieldID)) match {
          case Some(binding) => tableColumn.textProperty.bind(binding)
          case None => tableColumn.setText(fieldID.id)
        }
      }
      case other => tableColumn.setText(other.toString)
    }
    tableColumn
  }
  
  private def createColumnHeaderTableColumns(tableData:TableData):List[TableColumn[OpenAFTableRow,Any]] = {
    val columnHeaders = tableData.tableValues.columnHeaders
    val columnHeaderLayout = tableData.tableState.tableLayout.columnHeaderLayout
    val paths = columnHeaderLayout.paths
    val columnHeaderLayoutPathBreaks = columnHeaderLayout.columnHeaderLayoutPathBreaks
    val valueLookUp = tableData.tableValues.valueLookUp
    val numberOfPaths = columnHeaders.length
    val parentColumns = new ListBuffer[TableColumn[OpenAFTableRow,Any]]
    var runningColumnCount = 0
    (0 until numberOfPaths).foreach(pathIndex => {
      val pathColumnHeaders = columnHeaders(pathIndex)
      val path = paths(pathIndex)
      val pathFields = path.fields
      val numRows = pathFields.size
      val measureFieldOption = path.measureFieldOption
      val defaultRenderer = measureFieldOption match {
        case Some(measureField) => tableData.defaultRenderers(measureField)
        case None => BlankRenderer
      }
      val numColumns = pathColumnHeaders.length
      val tableColumns = new Array[TableColumn[OpenAFTableRow,Any]](numColumns)

      (0 until numRows).foreach(row => {
        val field = pathFields(row)
        val values = valueLookUp(field.id)
        (0 until numColumns).foreach(column => {
          val value = pathColumnHeaders(column)(row)

          if (row == 0 && column == 0 && pathIndex > 0 && field == paths(pathIndex - 1).fields(0)) {
            // If it is the first row and column in this path but not the very first path then we might be able to use
            // the previous parent column as the fields are the same
            val previousPathColumnHeaders = columnHeaders(pathIndex - 1)
            val previousValue = previousPathColumnHeaders(previousPathColumnHeaders.length - 1)(0)
            if (value == previousValue && !columnHeaderLayoutPathBreaks.contains(pathIndex)) {
              // The values for the same field are the same and we are not at a column header layout path boundary so
              // use the previous column
              tableColumns(0) = parentColumns.last
            } else {
              // Either the values are different or we are at a path boundary. Either way we need a new column
              tableColumns(0) = createColumnHeaderTableColumn(values(value))
              parentColumns += tableColumns(0)
            }
          } else if (column == 0) {
            val tableColumn = createColumnHeaderTableColumn(values(value))
            val existingTableColumn = tableColumns(0)
            if (existingTableColumn != null) {
              existingTableColumn.getColumns.add(tableColumn)
            }
            tableColumns(0) = tableColumn

            if (row == 0) {
              parentColumns += tableColumn
            }
          } else {
            val previousColumn = column - 1
            val previousValue = pathColumnHeaders(previousColumn)(row)
            def checkPreviousRow = {
              if (row > 0) {
                val existingTableColumn = tableColumns(column)
                val previousExistingTableColumn = tableColumns(previousColumn).getParentColumn
                existingTableColumn eq previousExistingTableColumn
              } else {
                true
              }
            }
            if (previousValue == value && checkPreviousRow) {
              tableColumns(column) = tableColumns(previousColumn)
            } else {
              val tableColumn = createColumnHeaderTableColumn(values(value))
              val existingTableColumn = tableColumns(column)
              if (existingTableColumn != null) {
                existingTableColumn.getColumns.add(tableColumn)
              }
              tableColumns(column) = tableColumn

              if (row == 0) {
                parentColumns += tableColumn
              }
            }
          }
          tableColumns(column).setCellValueFactory(new DefaultCellValueFactory(runningColumnCount + column))
          tableColumns(column).setCellFactory(new DefaultCellFactory(defaultRenderer))
        })
      })
      runningColumnCount += numColumns
    })

    parentColumns.toList.distinct
  }
}

class DefaultRowHeaderCellValueFactory extends Callback[CellDataFeatures[OpenAFTableRow,Int],ObservableValue[Int]] {
  def call(cellDataFeatures:CellDataFeatures[OpenAFTableRow,Int]) = {
    // Because row header columns are never nested this will successfully always return the correct index
    val index = cellDataFeatures.getTableView.getColumns.indexOf(cellDataFeatures.getTableColumn)
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Int](row.rowHeaderArray(index))
  }
}

class DefaultRowHeaderCellFactory[T](values:Array[Any], renderer:Renderer[T])
  extends Callback[TableColumn[OpenAFTableRow,Int],TableCell[OpenAFTableRow,Int]] {

  def call(tableColumn:TableColumn[OpenAFTableRow,Int]) = new TableCell[OpenAFTableRow,Int] {
    @tailrec private def checkColumnToTheLeft(rowIndex:Int, columnIndex:Int):Boolean = {
      if (columnIndex == 0) {
        false
      } else {
        val columnToTheLeft = getTableView.getColumns.get(columnIndex - 1)
        val different = columnToTheLeft.getCellData(rowIndex) != columnToTheLeft.getCellData(rowIndex - 1)
        if (different) {
          true
        } else {
          checkColumnToTheLeft(rowIndex, columnIndex - 1)
        }
      }
    }

    override def updateItem(intValue:Int, isEmpty:Boolean) {
      val shouldRender = !isEmpty && (Option(getTableRow).map(_.getIndex) match {
        case None => false
        case Some(rowIndex) => {
          if (rowIndex == 0) {
            true // Always render the value in the first row
          } else {
            if (getTableColumn.getCellData(rowIndex - 1) != intValue) {
              true // The value in the row above is different so render this value
            } else {
              // Check the values in the previous column to see if we need to render
              val columnIndex = getTableView.getColumns.indexOf(getTableColumn)
              checkColumnToTheLeft(rowIndex, columnIndex)
            }
          }
        }
      })

      if (shouldRender) {
        val value = values(intValue).asInstanceOf[T]
        setText(renderer.render(value))
      } else {
        setText(null)
      }
    }
  }
}

class DefaultCellValueFactory(index:Int) extends Callback[CellDataFeatures[OpenAFTableRow,Any],ObservableValue[Any]] {
  def call(cellDataFeatures:CellDataFeatures[OpenAFTableRow,Any]) = {
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Any](row.columnDataListBuffer.get(index))
  }
}

class DefaultCellFactory[T](renderer:Renderer[T]) extends Callback[TableColumn[OpenAFTableRow,Any],TableCell[OpenAFTableRow,Any]] {
  def call(tableColumn:TableColumn[OpenAFTableRow,Any]) = new TableCell[OpenAFTableRow,Any] {
    override def updateItem(anyValue:Any, isEmpty:Boolean) {
      if (isEmpty) {
        setText(null)
      } else {
        val value = anyValue.asInstanceOf[T]
        setText(renderer.render(value))
      }
    }
  }
}

class OpenAFTableRow(val rowHeaderArray:Array[Int]) {
  var columnDataListBuffer:util.ArrayList[Any] = null
}
