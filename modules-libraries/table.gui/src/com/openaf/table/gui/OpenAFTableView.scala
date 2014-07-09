package com.openaf.table.gui

import javafx.scene.control.{TableCell, TableColumn, TableView}
import javafx.beans.property.{ReadOnlyObjectWrapper, Property}
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.{FieldID, BlankRenderer, Renderer, TableData}
import javafx.collections.{ObservableMap, ObservableList, FXCollections}
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures
import scala.collection.mutable.ListBuffer
import scala.annotation.tailrec
import javafx.beans.binding.StringBinding

class OpenAFTableView(tableDataProperty:Property[TableData],
                      fieldBindings:ObservableMap[FieldID,StringBinding]) extends TableView[ObservableList[Any]] {
  getStyleClass.add("openaf-table-view")
  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      setUpTableView(newTableData)
    }
  })

  private def setUpTableView(tableData:TableData) {
    val rowHeaderFields = tableData.tableState.tableLayout.rowHeaderFields
    val rowHeaderTableColumns = rowHeaderFields.zipWithIndex.map{case (field,index) => {
      val tableColumn = new TableColumn[ObservableList[Any],Int]
      Option(fieldBindings.get(field.id)) match {
        case Some(binding) => tableColumn.textProperty.bind(binding)
        case None => tableColumn.setText(field.id.id)
      }
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
    var columnData:Array[Any] = null

    val numberOfRows = if (rowHeaderData.length > 0) {
      rowHeaderData.length
    } else {
      if (pathData.length > 0) pathData(0).length else 0
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
        columnData = pathData(path)(row)
        numberOfColumns = columnData.length
        while (column < numberOfColumns) {
          tableItem.add(columnData(column))
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

  private def createTableColumn(value:Any) = {
    val tableColumn = new TableColumn[ObservableList[Any],Any]
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

  private def createColumnHeaderTableColumns(tableData:TableData):List[TableColumn[ObservableList[Any],Any]] = {
    val columnHeaders = tableData.tableValues.columnHeaders
    val measureAreaLayout = tableData.tableState.tableLayout.measureAreaLayout
    val paths = measureAreaLayout.paths
    val measureAreaLayoutPathBreaks = measureAreaLayout.measureAreaLayoutPathBreaks
    val valueLookUp = tableData.tableValues.valueLookUp
    val numberOfPaths = columnHeaders.length
    val parentColumns = new ListBuffer[TableColumn[ObservableList[Any],Any]]
    val numberOfRowHeaders = tableData.tableState.tableLayout.rowHeaderFields.length
    var runningColumnCount = numberOfRowHeaders
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
      val tableColumns = new Array[TableColumn[ObservableList[Any],Any]](numColumns)

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
            if (value == previousValue && !measureAreaLayoutPathBreaks.contains(pathIndex)) {
              // The values for the same field are the same and we are not at a measure area layout path boundary so
              // use the previous column
              tableColumns(0) = parentColumns.last
            } else {
              // Either the values are different or we are at a path boundary. Either way we need a new column
              tableColumns(0) = createTableColumn(values(value))
              parentColumns += tableColumns(0)
            }
          } else if (column == 0) {
            val tableColumn = createTableColumn(values(value))
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
              val tableColumn = createTableColumn(values(value))
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

class DefaultCellValueFactory(index:Int)
  extends Callback[CellDataFeatures[ObservableList[Any],Any],ObservableValue[Any]] {

  def call(cellDataFeatures:CellDataFeatures[ObservableList[Any],Any]) = {
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Any](row.get(index))
  }
}

class DefaultCellFactory[T](renderer:Renderer[T])
  extends Callback[TableColumn[ObservableList[Any],Any],TableCell[ObservableList[Any],Any]] {

  def call(tableColumn:TableColumn[ObservableList[Any],Any]) = new TableCell[ObservableList[Any],Any] {
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