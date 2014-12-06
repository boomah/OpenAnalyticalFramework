package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.{DataPath, TableDataSource}

object TableDataGenerator {
  // This is far from idiomatic Scala. Written this way for speed.
  def tableData(tableStateNoKeys:TableState, tableDataSource:TableDataSource) = {
    val tableState = tableStateNoKeys.generateFieldKeys
    val result = tableDataSource.result(tableState)

    if (result.resultState.sortState.sortingRequired) {
      val sorter = new TableDataGeneratorSorter(result, tableState, tableDataSource)

      if (!result.resultState.sortState.filtersSorted) {
        sorter.sortFilterFieldValues()
      }

      if (!result.resultState.sortState.rowHeadersSorted) {
        sorter.sortRowHeaderAndFieldValues()
      }

      if (!result.resultState.sortState.columnHeadersSorted) {
        sorter.sortColumnHeaderAndFieldValues()
      }
    }

    val extraRowForRowHeaderFields = tableState.rowHeaderFields.nonEmpty && (result.numColumnHeaderRows == 0)
    val numRows = result.numRows + (if (extraRowForRowHeaderFields) 1 else 0)
    val rows = new Array[OpenAFTableRow](numRows)
    var rowCounter = 0
    var columnCounter = 0
    val blankRowHeaderValues = Array.fill(result.numRowHeaderColumns)(TableValues.NoValueInt)
    var row:OpenAFTableRow = null
    var rowHeaderKey:Array[Int] = null
    var key:DataPath = null

    // Populate the rows from the column headers
    while (rowCounter < result.numColumnHeaderRows) {
      row = new OpenAFTableRow(rowCounter, blankRowHeaderValues, new Array[Any](result.numColumnHeaderColumns))
      rows(rowCounter) = row

      while (columnCounter < result.numColumnHeaderColumns) {
        row.columnHeaderAndDataValues(columnCounter) = if (rowCounter < result.columnHeaderPaths(columnCounter).values.length) {
          result.columnHeaderPaths(columnCounter).values(rowCounter)
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
    val rowOffset = if (extraRowForRowHeaderFields) 1 + result.numColumnHeaderRows else result.numColumnHeaderRows
    
    // Populate the rows from the row header values and the data
    while (rowCounter < numRows) {
      rowHeaderKey = result.rowHeaderValues(rowCounter - rowOffset)
      row = new OpenAFTableRow(rowCounter, rowHeaderKey, new Array[Any](result.numColumnHeaderColumns))
      rows(rowCounter) = row

      while (columnCounter < result.numColumnHeaderColumns) {
        key = new DataPath(rowHeaderKey, result.columnHeaderPaths(columnCounter))
        row.columnHeaderAndDataValues(columnCounter) = result.data.getOrElse(key, NoValue)
        columnCounter += 1
      }

      columnCounter = 0
      rowCounter += 1
    }

    val fieldPathsIndexes = new Array[Int](result.numColumnHeaderColumns)
    columnCounter = 0
    while (columnCounter < result.numColumnHeaderColumns) {
      fieldPathsIndexes(columnCounter) = result.columnHeaderPaths(columnCounter).fieldsPathIndex
      columnCounter += 1
    }

    val fieldGroup = tableDataSource.fieldDefinitionGroups.fieldGroup
    val tableValues = TableValues(rows, fieldPathsIndexes, result.fieldValues, result.valueLookUp)
    val defaultRenderers:Map[FieldID,Renderer[_]] = tableState.tableLayout.allFields.map(field => {
      val fieldDefinition = tableDataSource.fieldDefinitionGroups.fieldDefinition(field.id)
      val renderer = if (field.fieldType.isDimension) fieldDefinition.renderer else fieldDefinition.combinedRenderer
      field.id -> NoValueAwareDelegatingRenderer(renderer)
    }).toMap

    TableData(fieldGroup, tableState, tableValues, defaultRenderers)
  }
}
