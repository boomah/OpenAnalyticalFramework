package com.openaf.table.gui

import javafx.scene.control._
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api._
import javafx.collections.FXCollections
import java.util

object OpenAFTableView {
  type TableColumnType = TableColumn[OpenAFTableRow,OpenAFTableRow]
  type OpenAFTableCell = TableCell[OpenAFTableRow,OpenAFTableRow]
}
import OpenAFTableView._

class OpenAFTableView(tableFields:OpenAFTableFields) extends TableView[OpenAFTableRow] {
  getStyleClass.add("openaf-table-view")
  getSelectionModel.setCellSelectionEnabled(true)
  getSelectionModel.setSelectionMode(SelectionMode.MULTIPLE)

  // I have to override the skin because the default one adds an additional 10 pixels when figuring out how width each 
  // column should be. This removes that padding. I have raised an issue so hopefully in some future JavaFX release this
  // can be removed.
  private val tableViewSkin = new com.sun.javafx.scene.control.skin.TableViewSkin[OpenAFTableRow](this) {
    def resizeColumnsToFitContent(startIndex:Int, endIndex:Int, maxRows:Int) {
      var index = startIndex
      while (index < endIndex) {
        resizeColumnToFitContent(getColumns.get(index), maxRows)
        index += 1
      }
    }
    override def resizeColumnToFitContent(tableColumn:TableColumn[OpenAFTableRow,_], maxRows:Int) {
      super.resizeColumnToFitContent(tableColumn, maxRows)
      tableColumn.impl_setWidth(tableColumn.getWidth - 10)
    }
  }
  override def createDefaultSkin() = tableViewSkin

  tableFields.tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      Option(oldTableData) match {
        case Some(tableData) if tableData.withDefaultRendererIds != newTableData.withDefaultRendererIds =>
          setUpTableView(newTableData)
        case None => setUpTableView(newTableData)
        case _ =>
          // TODO - don't need to set up the whole table here as only a renderer has changed.
          setUpTableView(newTableData)
      }
    }
  })

  private def setUpTableView(newTableData:TableData) {
    if (newTableData.rowHeaderFields.isEmpty && newTableData.columnHeaderLayout.isEmpty) {
      setItems(null)
      getColumns.clear()
    } else {
      val columnNames = new ExcelColumnNameIterator
      val columns = getColumns
      val numColumns = columns.size
      def column(index:Int) = columns.get(index).asInstanceOf[OpenAFTableColumn]

      var counter = 0

      // Stop the current renderers from doing anything
      while (counter < numColumns) {
        columns.get(counter).getCellFactory.asInstanceOf[OpenAFCellFactory].shouldUpdateItem = false
        counter += 1
      }

      val rowHeaderFields = newTableData.rowHeaderFields.toArray
      val numRowHeaderColumns = rowHeaderFields.length
      
      val paths = newTableData.columnHeaderLayout.paths
      val startRowHeaderValuesIndex = if (paths.isEmpty) 1 else paths.map(_.fields.size).max

      counter = 0

      if (numRowHeaderColumns <= numColumns) {
        // The number of columns required are already available. Reconfigure them and remove any extra ones.
        while (counter < numRowHeaderColumns) {
          val tableColumn = column(counter)
          tableColumn.setText(columnNames.next)
          tableColumn.columnIndex = counter
          val field = rowHeaderFields(counter)
          tableColumn.setCellFactory(new RowHeaderCellFactory(startRowHeaderValuesIndex, field, tableFields))
          counter += 1
        }
        columns.remove(numRowHeaderColumns, numColumns)

        // TODO - For now just always create the column header columns
        columns.addAll(createColumnHeaderTableColumns(newTableData, columnNames))

        setItems(FXCollections.observableArrayList[OpenAFTableRow](newTableData.tableValues.rows:_*))
        tableViewSkin.resizeColumnsToFitContent(0, numRowHeaderColumns, newTableData.numRows)
      } else {
        // Reconfigure the columns available already but some extra will be required.
        while (counter < numColumns) {
          val tableColumn = column(counter)
          tableColumn.setText(columnNames.next)
          tableColumn.columnIndex = counter
          val field = rowHeaderFields(counter)
          tableColumn.setCellFactory(new RowHeaderCellFactory(startRowHeaderValuesIndex, field, tableFields))
          counter += 1
        }

        val rowHeaderColumns = createRowHeaderTableColumns(newTableData, columnNames, numColumns)
        // TODO - For now just always create the column header columns
        rowHeaderColumns.addAll(createColumnHeaderTableColumns(newTableData, columnNames))
        columns.addAll(rowHeaderColumns)

        setItems(FXCollections.observableArrayList[OpenAFTableRow](newTableData.tableValues.rows:_*))
        tableViewSkin.resizeColumnsToFitContent(0, numColumns, newTableData.numRows)
      }
    }
  }

  private def createRowHeaderTableColumns(tableData:TableData, columnNames:Iterator[String], startIndex:Int) = {
    val rowHeaderFields = tableData.rowHeaderFields.toArray
    val numRowHeaderFields = rowHeaderFields.length
    val columns = new util.ArrayList[TableColumnType](tableData.rowHeaderFields.length)
    var rowHeaderFieldCounter = startIndex
    val paths = tableData.columnHeaderLayout.paths
    val startRowHeaderValuesIndex = if (paths.isEmpty) 1 else paths.map(_.fields.size).max
    while (rowHeaderFieldCounter < numRowHeaderFields) {
      val field = rowHeaderFields(rowHeaderFieldCounter)
      val tableColumn = new OpenAFTableColumn
      tableColumn.columnIndex = rowHeaderFieldCounter
      tableColumn.setText(columnNames.next)
      tableColumn.setCellFactory(new RowHeaderCellFactory(startRowHeaderValuesIndex, field, tableFields))
      columns.add(tableColumn)
      rowHeaderFieldCounter += 1
    }
    columns
  }

  private def createColumnHeaderTableColumns(tableData:TableData, columnNames:Iterator[String]) = {
    val numColumns = tableData.tableValues.fieldPathsIndexes.length
    val columns = new util.ArrayList[TableColumnType](numColumns)
    val paths = tableData.columnHeaderLayout.paths
    val maxPathLength = if (paths.isEmpty) 0 else paths.map(_.fields.size).max
    val pathsArray = paths.toArray
    val fieldPathIndexes = tableData.tableValues.fieldPathsIndexes
    val valueLookUp = tableData.tableValues.valueLookUp
    val allPathValueLookUps = paths.map(path => path.fields.map(field => valueLookUp(field.id)).toArray).toArray

    var tableColumn:OpenAFTableColumn = null
    var valueLookUps:Array[Array[Any]] = null

    var columnCounter = 0

    while (columnCounter < numColumns) {
      tableColumn = new OpenAFTableColumn
      tableColumn.columnIndex = columnCounter
      tableColumn.setText(columnNames.next.toString)

      valueLookUps = allPathValueLookUps(fieldPathIndexes(columnCounter))

      tableColumn.setCellFactory(
        new ColumnHeaderAndDataCellFactory(valueLookUps, fieldPathIndexes, pathsArray, maxPathLength, tableFields)
      )

      columns.add(tableColumn)
      columnCounter += 1
    }
    columns
  }
}
