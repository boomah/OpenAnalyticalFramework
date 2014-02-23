package com.openaf.table.gui

import javafx.scene.control.{TableCell, TableColumn, TableView}
import javafx.beans.property.{ReadOnlyObjectWrapper, Property}
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.{Renderer, TableData}
import javafx.collections.{ObservableList, FXCollections}
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import scala.collection.mutable.ListBuffer

class OpenAFTableView(tableDataProperty:Property[TableData]) extends TableView[ObservableList[Any]] {
  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      setUpTableView(newTableData)
    }
  })

  private def setUpTableView(tableData:TableData) {
    val rowHeaderFields = tableData.tableState.tableLayout.rowHeaderFields
    val rowHeaderTableColumns = rowHeaderFields.zipWithIndex.map{case (field,index) => {
      val tableColumn = new TableColumn[ObservableList[Any],Int](field.displayName)
      tableColumn.setCellValueFactory(new DefaultRowHeaderCellValueFactory(index))
      val values = tableData.tableValues.valueLookUp(rowHeaderFields(index).id)
      val rowHeaderField = rowHeaderFields(index)
      val defaultRenderer = tableData.defaultRenderers(rowHeaderField)
      val cellFactory = new DefaultRowHeaderCellFactory(values, defaultRenderer)
      tableColumn.setCellFactory(cellFactory)
      tableColumn
    }}
    val columnHeaderTableColumns = createColumnHeaderTableColumns(tableData)

    getColumns.clear()
    getColumns.addAll(rowHeaderTableColumns ::: columnHeaderTableColumns :_*)

    val tableItems = FXCollections.observableArrayList[ObservableList[Any]]

    val rowHeaderData = tableData.tableValues.rowHeaders

    val pathData = tableData.tableValues.data
    val numberOfPaths = pathData.length
    var path = 0
    var column = 0
    var numberOfColumns = 0

    val numberOfRows = if (rowHeaderData.length > 0) {
      rowHeaderData.length
    } else {
      if (pathData.length > 0) {
        val firstPathData = pathData(0)
        if (firstPathData.length > 0) {
          firstPathData(0).length
        } else {
          0
        }
      } else {
        0
      }
    }
    var row = 0
    while (row < numberOfRows) {

      val tableItem = if (rowHeaderData.length > 0) {
        val rowHeaderArray = rowHeaderData(row)
        FXCollections.observableArrayList[Any](rowHeaderArray :_*)
      } else {
        if (rowHeaderFields.isEmpty) {
          FXCollections.observableArrayList[Any]
        } else {
          FXCollections.observableArrayList[Any](List.fill(rowHeaderFields.length)(0))
        }
      }

      while (path < numberOfPaths) {
        numberOfColumns = pathData(path).length
        while (column < numberOfColumns) {
          tableItem.add(pathData(path)(column)(row))
          column += 1
        }
        column = 0
        path += 1
      }
      path = 0
      tableItems.add(tableItem)
      row += 1
    }

    setItems(tableItems)
  }

  private def createColumnHeaderTableColumns(tableData:TableData):List[TableColumn[ObservableList[Any],Any]] = {
    val columnHeaders = tableData.tableValues.columnHeaders
    val paths = tableData.tableState.tableLayout.measureAreaLayout.paths
    val valueLookUp = tableData.tableValues.valueLookUp
    val numberOfPaths = columnHeaders.length
    val parentColumns = new ListBuffer[TableColumn[ObservableList[Any],Any]]
    val numberOfRowHeaders = tableData.tableState.tableLayout.rowHeaderFields.length
    var grandColumnCount = numberOfRowHeaders
    (0 until numberOfPaths).foreach(pathIndex => {
      val pathColumnHeaders = columnHeaders(pathIndex)

      val numColumns = pathColumnHeaders.length
      val tableColumns = new Array[TableColumn[ObservableList[Any],Any]](numColumns)
      val pathFields = paths(pathIndex).fields
      val numRows = pathFields.size
      (0 until numRows).foreach(row => {
        val field = pathFields(row)
        val values = valueLookUp(field.id)
        grandColumnCount = numberOfRowHeaders
        (0 until numColumns).foreach(column => {
          val value = pathColumnHeaders(column)(row)

          if (row == 0 && column == 0 && pathIndex > 0 && field == paths(pathIndex - 1).fields(0)) {
            val previousPathColumnHeaders = columnHeaders(pathIndex - 1)
            val previousValue = previousPathColumnHeaders(previousPathColumnHeaders.length - 1)(0)
            if (value == previousValue) {
              tableColumns(0) = parentColumns.last
            } else {
              tableColumns(0) = new TableColumn[ObservableList[Any],Any](values(value).toString)
            }
          } else if (column == 0) {
            val tableColumn = new TableColumn[ObservableList[Any],Any](values(value).toString)
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
            if (previousValue == value) {
              tableColumns(column) = tableColumns(previousColumn)
            } else {
              val tableColumn = new TableColumn[ObservableList[Any],Any](values(value).toString)
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
          tableColumns(column).setCellValueFactory(new DefaultCellValueFactory(grandColumnCount))
          grandColumnCount += 1
        })
      })
    })

    parentColumns.toList.distinct
  }
}

class DefaultRowHeaderCellValueFactory(index:Int)
  extends Callback[CellDataFeatures[ObservableList[Any],Int],ObservableValue[Int]] {

  def call(cellDataFeatures:CellDataFeatures[ObservableList[Any],Int]) = {
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Int](row.get(index).asInstanceOf[Int])
  }
}

class DefaultRowHeaderCellFactory[T](values:Array[Any], renderer:Renderer[T])
  extends Callback[TableColumn[ObservableList[Any],Int],TableCell[ObservableList[Any],Int]] {

  def call(tableColumn:TableColumn[ObservableList[Any],Int]) = new TableCell[ObservableList[Any],Int] {
    override def updateItem(intValue:Int, isEmpty:Boolean) {
      if (isEmpty) {
        setText(null)
      } else {
        val value = values(intValue).asInstanceOf[T]
        setText(renderer.render(value))
      }
    }
  }
}

class DefaultCellValueFactory(index:Int)
  extends Callback[CellDataFeatures[ObservableList[Any],Any],ObservableValue[Any]] {

  def call(cellDataFeatures:CellDataFeatures[ObservableList[Any],Any]) = {
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Any](row.get(index))
  }
}