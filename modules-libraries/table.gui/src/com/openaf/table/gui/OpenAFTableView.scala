package com.openaf.table.gui

import javafx.scene.control.{Cell, TableCell, TableColumn, TableView}
import javafx.beans.property.{ReadOnlyObjectWrapper, Property}
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.{Renderer, TableData}
import javafx.collections.{ObservableList, FXCollections}
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import scala.collection.mutable.ListBuffer

class OpenAFTableView(tableDataProperty:Property[TableData]) extends TableView[ObservableList[Int]] {
  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      setUpTableView(newTableData)
    }
  })

  private def setUpTableView(tableData:TableData) {
    val rowHeaderFields = tableData.tableState.tableLayout.rowHeaderFields
    val rowHeaderTableColumns = rowHeaderFields.map(field => new TableColumn[ObservableList[Int],Int](field.displayName))
    val columnHeaderTableColumns = createColumnHeaderTableColumns(tableData)

    getColumns.clear()
    getColumns.addAll(rowHeaderTableColumns ::: columnHeaderTableColumns :_*)

    val rowHeaderData = FXCollections.observableArrayList[ObservableList[Int]]()
    tableData.tableValues.rowHeaders.foreach(rowArray => {
      val rowHeaderRow = FXCollections.observableArrayList(rowArray :_*)
      rowHeaderData.add(rowHeaderRow)
    })
    setItems(rowHeaderData)

    rowHeaderTableColumns.zipWithIndex.foreach{case (column, index) => {
      column.setCellValueFactory(new DefaultCellValueFactory(index))
      val values = tableData.tableValues.valueLookUp(rowHeaderFields(index).id)
      val rowHeaderField = rowHeaderFields(index)
      val defaultRenderer = tableData.defaultRenderers(rowHeaderField)
      val cellFactory = new DefaultCellFactory(values, defaultRenderer)
      column.setCellFactory(cellFactory)
    }}
  }

  private def createColumnHeaderTableColumns(tableData:TableData):List[TableColumn[ObservableList[Int],Int]] = {
    val columnHeaders = tableData.tableValues.columnHeaders
    val paths = tableData.tableState.tableLayout.measureAreaLayout.paths
    val valueLookUp = tableData.tableValues.valueLookUp
    val numberOfPaths = columnHeaders.length
    val parentColumns = new ListBuffer[TableColumn[ObservableList[Int],Int]]
    (0 until numberOfPaths).foreach(pathIndex => {
      val pathColumnHeaders = columnHeaders(pathIndex)

      val numColumns = pathColumnHeaders.length
      val tableColumns = new Array[TableColumn[ObservableList[Int],Int]](numColumns)
      val pathFields = paths(pathIndex).fields
      val numRows = pathFields.size
      (0 until numRows).foreach(row => {
        val field = pathFields(row)
        val values = valueLookUp(field.id)
        (0 until numColumns).foreach(column => {
          val value = pathColumnHeaders(column)(row)

          if (row == 0 && column == 0 && pathIndex > 0 && field == paths(pathIndex - 1).fields(0)) {
            val previousPathColumnHeaders = columnHeaders(pathIndex - 1)
            val previousValue = previousPathColumnHeaders(previousPathColumnHeaders.length - 1)(0)
            if (value == previousValue) {
              tableColumns(0) = parentColumns.last
            } else {
              tableColumns(0) = new TableColumn[ObservableList[Int],Int](values(value).toString)
            }
          } else if (column == 0) {
            val tableColumn = new TableColumn[ObservableList[Int],Int](values(value).toString)
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
              val tableColumn = new TableColumn[ObservableList[Int],Int](values(value).toString)
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
        })
      })
    })

    parentColumns.toList.distinct
  }
}

class DefaultCellValueFactory(index:Int)
  extends Callback[CellDataFeatures[ObservableList[Int],Int],ObservableValue[Int]] {

  def call(cellDataFeatures:CellDataFeatures[ObservableList[Int],Int]) = {
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Int](row.get(index))
  }
}

class DefaultCellFactory[T](values:Array[Any], renderer:Renderer[T])
  extends Callback[TableColumn[ObservableList[Int],Int],TableCell[ObservableList[Int],Int]] {

  def call(tableColumn:TableColumn[ObservableList[Int],Int]) = new TableCell[ObservableList[Int],Int] {
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

trait CellFactory[V] {
  def cell(values:Array[V]):Cell[Int]
}