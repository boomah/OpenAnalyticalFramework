package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.{DataPath, TableDataSource}
import java.util

object TableDataGenerator {
  // This is far from idiomatic Scala. Written this way for speed.
  def tableData(tableStateNoKeys:TableState, tableDataSource:TableDataSource) = {
    val tableState = tableStateNoKeys.generateFieldKeys
    val result = tableDataSource.result(tableState)
    def fieldDefinition(id:FieldID) = tableDataSource.fieldDefinitionGroups.fieldDefinition(id)

    if (!result.resultState.filterState.isFiltered) {
      // TODO - filter here
    }

    if (!result.resultState.totalsState.totalsAdded) {
      // TODO - add totals here
    }

    if (!result.resultState.sortState.filtersSorted) {
      val filterFieldIDs = tableState.tableLayout.filterFieldIDs
      val filterFieldDefinitions = filterFieldIDs.map(fieldDefinition).toArray
      val filterLookUps = filterFieldIDs.map(result.valueLookUp).toArray

      tableState.filterFields.zipWithIndex.foreach{case (field,i) => {
        val fieldValues = result.fieldValues.values(field)
        val fieldDefinition = filterFieldDefinitions(i)
        val ordering = fieldDefinition.ordering
        val lookUp = filterLookUps(i).asInstanceOf[Array[fieldDefinition.V]]
        FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
      }}
    }

    val rowHeaderFieldIDs = tableState.tableLayout.rowHeaderFieldIDs
    val rowHeaderFieldDefinitions = rowHeaderFieldIDs.map(fieldDefinition).toArray
    val rowHeaderLookUps = rowHeaderFieldIDs.map(result.valueLookUp).toArray

    val rowHeaderValues = result.rowHeaderValues
    if (!result.resultState.sortState.rowHeadersSorted) {
      util.Arrays.sort(
        rowHeaderValues,
        new RowHeaderComparator(tableState.tableLayout.rowHeaderFields.toArray, rowHeaderFieldDefinitions,
          rowHeaderLookUps)
      )

      tableState.rowHeaderFields.zipWithIndex.foreach{case (field,i) => {
        val fieldValues = result.fieldValues.values(field)
        val fieldDefinition = rowHeaderFieldDefinitions(i)
        val ordering = fieldDefinition.ordering
        val lookUp = rowHeaderLookUps(i).asInstanceOf[Array[fieldDefinition.V]]
        FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
      }}
    }

    if (!result.resultState.sortState.columnHeadersSorted) {
      val fieldPaths = tableState.columnHeaderLayout.paths
      val pathIndexToFields = new Array[Array[Field[_]]](fieldPaths.length)
      fieldPaths.zipWithIndex.foreach{case (path,i) => pathIndexToFields(i) = path.fields.toArray}
      val columnHeaderFields = tableState.columnHeaderLayout.allFields.toSet
      val columnHeaderFieldKeyFieldDefinitions = new Array[FieldDefinition](columnHeaderFields.size)
      val columnHeaderFieldKeyLookUps = new Array[Array[Any]](columnHeaderFields.size)
      columnHeaderFields.foreach(field => {
        columnHeaderFieldKeyFieldDefinitions(field.key.number) = fieldDefinition(field.id)
        columnHeaderFieldKeyLookUps(field.key.number) = result.valueLookUp(field.id)
      })

      util.Arrays.sort(
        result.columnHeaderPaths,
        new ColumnHeaderPathComparator(pathIndexToFields, columnHeaderFieldKeyFieldDefinitions, columnHeaderFieldKeyLookUps)
      )

      columnHeaderFields.foreach(field => {
        val fieldValues = result.fieldValues.values(field)
        val fieldDefinition = columnHeaderFieldKeyFieldDefinitions(field.key.number)
        val ordering = fieldDefinition.ordering
        val lookUp = columnHeaderFieldKeyLookUps(field.key.number).asInstanceOf[Array[fieldDefinition.V]]
        FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
      })
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
      rowHeaderKey = rowHeaderValues(rowCounter - rowOffset)
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
