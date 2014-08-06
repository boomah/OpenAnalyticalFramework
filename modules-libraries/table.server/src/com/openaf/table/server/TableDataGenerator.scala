package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.{IntArrayWrapperKey, TableDataSource}
import java.util.Comparator
import java.util

object TableDataGenerator {
  // This is far from idiomatic Scala. Written this way for speed.
  def tableData(tableState:TableState, tableDataSource:TableDataSource) = {
    val result = tableDataSource.result(tableState)

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
    val numRowHeaderValues = rowHeaderValues.length
    var rowHeaderCounter = 0

    val paths = tableState.tableLayout.columnHeaderLayout.paths
    val allPathData = result.pathData
    val numPaths = allPathData.length
    var pathCounter = 0

    val allColHeaderValues = paths.zipWithIndex.map{case (path,pathIndex) => {
      val pathData = allPathData(pathIndex)
      val colHeaderValues = pathData.colHeaderValues
      if (!result.resultState.sortState.pathDataSorted(pathIndex)) {
        val colHeaderFieldDefinitions = path.fields.map(field => {
          tableDataSource.fieldDefinitionGroups.fieldDefinition(field.id)
        }).toArray
        val colHeaderLookUps = path.fields.map(field => result.valueLookUp(field.id)).toArray
        util.Arrays.sort(
          colHeaderValues,
          new TableDataGeneratorComparator(path.fields.toArray, colHeaderFieldDefinitions, colHeaderLookUps)
        )

        path.fields.zipWithIndex.foreach{case (field,i) => {
          val fieldValues = result.fieldValues.values(field)
          val fieldDefinition = colHeaderFieldDefinitions(i)
          val ordering = fieldDefinition.ordering
          val lookUp = colHeaderLookUps(i).asInstanceOf[Array[fieldDefinition.V]]
          FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
        }}
      }
      colHeaderValues
    }}.toArray
    val colHeaderValuesLengths = allColHeaderValues.map(_.length)
    var colHeaderCounter = 0

    var dataForPath:Map[IntArrayWrapperKey, Any] = null
    var numColHeaders = -1
    var colHeaderValues:Array[Array[Int]] = null

    val dataForPaths = allPathData.map(_.data)

    var rowHeaderKey:Array[Int] = null
    var key:IntArrayWrapperKey = null
    val data:Array[Array[Array[Any]]] = Array.fill(numPaths)(new Array(numRowHeaderValues))

    while (rowHeaderCounter < numRowHeaderValues) {
      rowHeaderKey = rowHeaderValues(rowHeaderCounter)
      while (pathCounter < numPaths) {
        colHeaderValues = allColHeaderValues(pathCounter)
        numColHeaders = colHeaderValuesLengths(pathCounter)
        data(pathCounter)(rowHeaderCounter) = new Array[Any](numColHeaders)
        dataForPath = dataForPaths(pathCounter)
        while (colHeaderCounter < numColHeaders) {
          key = new IntArrayWrapperKey(rowHeaderKey, colHeaderValues(colHeaderCounter))
          data(pathCounter)(rowHeaderCounter)(colHeaderCounter) = dataForPath.getOrElse(key, NoValue)
          colHeaderCounter += 1
        }
        colHeaderCounter = 0
        pathCounter += 1
      }
      pathCounter = 0
      rowHeaderCounter += 1
    }

    val fieldGroup = tableDataSource.fieldDefinitionGroups.fieldGroup

    val tableValues = TableValues(result.rowHeaderValues, allColHeaderValues, data, result.fieldValues, result.valueLookUp)

    val defaultRenderers:Map[Field[_],Renderer[_]] = tableState.tableLayout.allFields.map(field => {
      val fieldDefinition = tableDataSource.fieldDefinitionGroups.fieldDefinition(field.id)
      val renderer = if (field.fieldType.isDimension) fieldDefinition.renderer else fieldDefinition.combinedRenderer
      field -> NoValueAwareDelegatingRenderer(renderer)
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