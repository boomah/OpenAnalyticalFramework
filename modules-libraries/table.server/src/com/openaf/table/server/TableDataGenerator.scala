package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.{IntArrayWrapperKey, TableDataSource}
import java.util.Comparator
import java.util
import TableValues._

object TableDataGenerator {
  // This is far from idiomatic Scala. Written this way for speed.
  def tableData(tableStateNoKeys:TableState, tableDataSource:TableDataSource) = {
    val tableState = tableStateNoKeys.generateFieldKeys
    val result = tableDataSource.result(tableState)

    if (!result.resultState.filterState.isFiltered) {
      // TODO - filter here
    }

    if (!result.resultState.totalsState.totalsAdded) {
      // TODO - add totals here
    }

    if (!result.resultState.sortState.filtersSorted) {
      val filterFieldIDs = tableState.tableLayout.filterFieldIDs
      val filterFieldDefinitions = filterFieldIDs.map(fieldID => {
        tableDataSource.fieldDefinitionGroups.fieldDefinition(fieldID)
      }).toArray
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
    val rowHeaderFieldDefinitions = rowHeaderFieldIDs.map(fieldID => {
      tableDataSource.fieldDefinitionGroups.fieldDefinition(fieldID)
    }).toArray
    val rowHeaderLookUps = rowHeaderFieldIDs.map(result.valueLookUp).toArray

    val rowHeaderValues = result.rowHeaderValues
    if (!result.resultState.sortState.rowHeadersSorted) {
      util.Arrays.sort(
        rowHeaderValues,
        new TableDataGeneratorComparator(tableState.tableLayout.rowHeaderFields.toArray, rowHeaderFieldDefinitions,
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

    val paths = tableState.tableLayout.columnHeaderLayout.paths
    val allPathData = result.pathData
    val numPaths = allPathData.length
    val allColHeaderValues = new Array[Array[Array[Int]]](numPaths)
    var columnHeaderValuesForPath:Array[Array[Int]] = null
    var path:ColumnHeaderLayoutPath = null
    var pathCounter = 0

    while (pathCounter < numPaths) {
      path = paths(pathCounter)
      columnHeaderValuesForPath = allPathData(pathCounter).colHeaderValues
      if (!result.resultState.sortState.pathDataSorted(pathCounter)) {
        val colHeaderFieldDefinitions = path.fields.map(field => {
          tableDataSource.fieldDefinitionGroups.fieldDefinition(field.id)
        }).toArray
        val colHeaderLookUps = path.fields.map(field => result.valueLookUp(field.id)).toArray
        util.Arrays.sort(
          columnHeaderValuesForPath,
          new TableDataGeneratorComparator(path.fields.toArray, colHeaderFieldDefinitions, colHeaderLookUps)
        )
        allColHeaderValues(pathCounter) = columnHeaderValuesForPath

        path.fields.zipWithIndex.foreach{case (field,i) => {
          val fieldValues = result.fieldValues.values(field)
          val fieldDefinition = colHeaderFieldDefinitions(i)
          val ordering = fieldDefinition.ordering
          val lookUp = colHeaderLookUps(i).asInstanceOf[Array[fieldDefinition.V]]
          FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
        }}
      }

      pathCounter += 1
    }
    pathCounter = 0

    val extraRowForRowHeaderFields = tableState.rowHeaderFields.nonEmpty && (result.numColumnHeaderRows == 0)
    val numRows = result.numRows + (if (extraRowForRowHeaderFields) 1 else 0)
    val rows = new Array[OpenAFTableRow](numRows)
    var rowCounter = 0
    var columnCounter = 0
    val blankRowHeaderValues = Array.fill(result.numRowHeaderColumns)(TableValues.NoValueInt)
    var row:OpenAFTableRow = null
    var rowHeaderKey:Array[Int] = null
    var key:IntArrayWrapperKey = null
    var columnHeaderValues:Array[Int] = null
    val dataForPaths = allPathData.map(_.data)
    var dataForPath:Map[IntArrayWrapperKey, Any] = null
    var numColumnsPerPath = 0
    var columnsPerPathCounter = 0

    // Populate the rows from the column headers
    while (rowCounter < result.numColumnHeaderRows) {
      row = new OpenAFTableRow(rowCounter, blankRowHeaderValues, new Array[Any](result.numColumnHeaderColumns))
      rows(rowCounter) = row

      while (pathCounter < numPaths) {
        columnHeaderValuesForPath = allColHeaderValues(pathCounter)
        numColumnsPerPath = columnHeaderValuesForPath.length
        while (columnsPerPathCounter < numColumnsPerPath) {
          columnHeaderValues = columnHeaderValuesForPath(columnsPerPathCounter)
          row.columnHeaderAndDataValues(columnCounter) = if (rowCounter < columnHeaderValues.length) {
            columnHeaderValues(rowCounter)
          } else {
            TableValues.NoValueInt
          }

          columnCounter += 1
          columnsPerPathCounter += 1
        }

        columnsPerPathCounter = 0
        pathCounter += 1
      }

      pathCounter = 0
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

      while (pathCounter < numPaths) {
        columnHeaderValuesForPath = allColHeaderValues(pathCounter)
        numColumnsPerPath = columnHeaderValuesForPath.length
        dataForPath = dataForPaths(pathCounter)
        while (columnsPerPathCounter < numColumnsPerPath) {
          columnHeaderValues = columnHeaderValuesForPath(columnsPerPathCounter)
          key = new IntArrayWrapperKey(rowHeaderKey, columnHeaderValues)
          row.columnHeaderAndDataValues(columnCounter) = dataForPath.getOrElse(key, NoValue)

          columnCounter += 1
          columnsPerPathCounter += 1
        }

        columnsPerPathCounter = 0
        pathCounter += 1
      }

      pathCounter = 0
      columnCounter = 0
      rowCounter += 1
    }

    val fieldGroup = tableDataSource.fieldDefinitionGroups.fieldGroup
    val columnsPerPath = allColHeaderValues.map(_.length)
    val tableValues = TableValues(rows, columnsPerPath, result.fieldValues, result.valueLookUp)
    val defaultRenderers:Map[FieldID,Renderer[_]] = tableState.tableLayout.allFields.map(field => {
      val fieldDefinition = tableDataSource.fieldDefinitionGroups.fieldDefinition(field.id)
      val renderer = if (field.fieldType.isDimension) fieldDefinition.renderer else fieldDefinition.combinedRenderer
      field.id -> NoValueAwareDelegatingRenderer(renderer)
    }).toMap

    TableData(fieldGroup, tableState, tableValues, defaultRenderers)
  }
}

class TableDataGeneratorComparator(fields:Array[Field[_]], fieldDefinitions:Array[FieldDefinition],
                                   lookUps:Array[Array[Any]]) extends Comparator[Array[Int]] {
  require(fieldDefinitions.length == lookUps.length, "Must be the same length")

  private var length = -1
  private var counter = 0
  private var sorted = false
  private var result = 0
  private var lookUp:Array[Any] = _
  private var value1 = -1
  private var value2 = -1

  def compare(array1:Array[Int], array2:Array[Int]) = {
    length = array1.length
    sorted = false
    counter = 0
    while (!sorted && counter < length) {
      value1 = array1(counter)
      value2 = array2(counter)
      result = if (value1 != value2) {
        if (value1 == TotalTopInt || value2 == TotalBottomInt) {
          -1
        } else if (value2 == TotalTopInt || value1 == TotalBottomInt) {
          1
        } else {
          val fieldDefinition = fieldDefinitions(counter)
          lookUp = lookUps(counter)
          if (fields(counter).sortOrder == SortOrder.Ascending) {
            fieldDefinition.ordering.compare(
              lookUp(value1).asInstanceOf[fieldDefinition.V],
              lookUp(value2).asInstanceOf[fieldDefinition.V]
            )
          } else {
            fieldDefinition.ordering.compare(
              lookUp(value2).asInstanceOf[fieldDefinition.V],
              lookUp(value1).asInstanceOf[fieldDefinition.V]
            )
          }
        }
      } else {
        0
      }
      if (result != 0) {
        sorted = true
      } else {
        counter += 1
      }
    }
    result
  }
}