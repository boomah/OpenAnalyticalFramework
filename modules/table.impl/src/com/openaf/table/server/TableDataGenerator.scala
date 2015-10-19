package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.PivotData

object TableDataGenerator {
  // This is far from idiomatic Scala. Written this way for speed.
  def tableData(pivotData:PivotData) = {
    val sorter = new TableDataGeneratorSorter(pivotData)
    sorter.sortFilterFieldValues()
    sorter.sortRowHeaderAndFieldValues()
    sorter.sortColumnHeaderAndFieldValues()

    val tableState = pivotData.tableState
    val extraRowForRowHeaderFields = tableState.rowHeaderFields.nonEmpty && (pivotData.numColumnHeaderRows == 0)
    val numRows = pivotData.numRows + (if (extraRowForRowHeaderFields) 1 else 0)
    val rows = new Array[OpenAFTableRow](numRows)
    var rowCounter = 0
    var columnCounter = 0
    val blankRowHeaderValues = Array.fill(pivotData.numRowHeaderColumns)(TableValues.NoValueInt)
    var row:OpenAFTableRow = null
    var rowHeaderKey:Array[Int] = null

    // Populate the rows from the column headers
    while (rowCounter < pivotData.numColumnHeaderRows) {
      row = new OpenAFTableRow(rowCounter, blankRowHeaderValues, new Array[Any](pivotData.numColumnHeaderColumns))
      rows(rowCounter) = row

      while (columnCounter < pivotData.numColumnHeaderColumns) {
        row.columnHeaderAndDataValues(columnCounter) = if (rowCounter < pivotData.columnHeaderValues(columnCounter).length - 1) {
          pivotData.columnHeaderValues(columnCounter)(rowCounter)
        } else {
          TableValues.NoValueInt
        }
        columnCounter += 1
      }

      columnCounter = 0
      rowCounter += 1
    }
    
    // Add the row header fields
    val rowHeaderFieldsArray = Array.fill(tableState.rowHeaderFields.length)(0)
    if (extraRowForRowHeaderFields) {
      rows(0) = new OpenAFTableRow(0, rowHeaderFieldsArray, Array.empty)
      rowCounter = 1
    } else if (tableState.rowHeaderFields.nonEmpty) {
      val row = rowCounter - 1
      rows(row) = new OpenAFTableRow(row, rowHeaderFieldsArray, rows(row).columnHeaderAndDataValues)
    }
    val rowOffset = if (extraRowForRowHeaderFields) 1 + pivotData.numColumnHeaderRows else pivotData.numColumnHeaderRows
    
    // Populate the rows from the row header values and the data
    while (rowCounter < numRows) {
      rowHeaderKey = pivotData.rowHeaderValues(rowCounter - rowOffset)
      row = new OpenAFTableRow(rowCounter, rowHeaderKey, new Array[Any](pivotData.numColumnHeaderColumns))
      rows(rowCounter) = row

      while (columnCounter < pivotData.numColumnHeaderColumns) {
        row.columnHeaderAndDataValues(columnCounter) = pivotData.aggregator(rowHeaderKey, pivotData.columnHeaderValues(columnCounter))
        columnCounter += 1
      }

      columnCounter = 0
      rowCounter += 1
    }

    val fieldPathsIndexes = new Array[Int](pivotData.numColumnHeaderColumns)
    columnCounter = 0
    while (columnCounter < pivotData.numColumnHeaderColumns) {
      val columnHeaderValues = pivotData.columnHeaderValues(columnCounter)
      fieldPathsIndexes(columnCounter) = columnHeaderValues(columnHeaderValues.length - 1)
      columnCounter += 1
    }

    val fieldGroup = pivotData.fieldDefinitionGroups.fieldGroup
    val tableValues = TableValues(rows, fieldPathsIndexes, pivotData.fieldValues, pivotData.valueLookUp)

    TableData(fieldGroup, tableState, tableValues)
  }
}
