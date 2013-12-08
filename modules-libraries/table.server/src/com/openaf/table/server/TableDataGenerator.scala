package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.TableDataSource
import java.util.Comparator
import java.util

object TableDataGenerator {
  // This is far from idiomatic Scala. Written this way for speed.
  // TODO - remove the need to keep converting to and from lists
  def tableData(tableState:TableState, tableDataSource:TableDataSource) = {
    val result = tableDataSource.result(tableState)

    val rowHeaderFieldIDs = tableState.tableLayout.rowHeaderFieldIDs
    val rowHeaderFieldDefinitions = rowHeaderFieldIDs.map(fieldID => {
      tableDataSource.fieldDefinitionGroup.fieldDefinition(fieldID)
    }).toArray
    val rowHeaderLookUps = rowHeaderFieldIDs.map(result.valueLookUp).toArray

    val rowHeaderValues = result.rowHeaderValues
    if (!result.resultDetails.sortDetails.rowHeadersSorted) {
      util.Arrays.sort(
        rowHeaderValues,
        new TableDataGeneratorComparator(tableState.tableLayout.rowHeaderFields.toArray, rowHeaderFieldDefinitions,
          rowHeaderLookUps)
      )
    }
    val numRowHeaderValues = rowHeaderValues.length
    var rowHeaderCounter = 0

    val paths = tableState.tableLayout.measureAreaLayout.reversePaths
    val allPathData = result.pathData
    val numPaths = allPathData.length
    var pathCounter = 0

    val allColHeaderValues = paths.zipWithIndex.map{case (path, i) => {
      val pathData = allPathData(i)
      val colHeaderValues = pathData.colHeaderValues
      if (!result.resultDetails.sortDetails.pathDataSorted(i)) {
        val colHeaderFieldDefinitions = path.fields.map(field => {
          tableDataSource.fieldDefinitionGroup.fieldDefinition(field.id)
        }).toArray
        val colHeaderLookUps = path.fields.map(field => result.valueLookUp(field.id)).toArray
        util.Arrays.sort(
          colHeaderValues,
          new TableDataGeneratorComparator(path.fields.toArray, colHeaderFieldDefinitions, colHeaderLookUps)
        )
      }
      colHeaderValues
    }}.toArray
    val colHeaderValuesLengths = allColHeaderValues.map(_.length)
    var colHeaderCounter = 0

    var dataForPath:Map[(List[Int], List[Int]), Any] = null
    var numColHeaders = -1
    var colHeaderValues:Array[Array[Int]] = null

    val dataForPaths = allPathData.map(_.data)

    var rowHeaderKey:List[Int] = Nil
    var colHeaderKey:List[Int] = Nil
    var key:(List[Int],List[Int]) = null
    var value:Any = null
    val data:Array[Array[Array[Any]]] = Array.fill(numPaths)(new Array(numRowHeaderValues))

    while (rowHeaderCounter < numRowHeaderValues) {
      rowHeaderKey = rowHeaderValues(rowHeaderCounter).toList
      while (pathCounter < numPaths) {
        colHeaderValues = allColHeaderValues(pathCounter)
        numColHeaders = colHeaderValuesLengths(pathCounter)
        data(pathCounter)(rowHeaderCounter) = new Array[Any](numColHeaders)
        dataForPath = dataForPaths(pathCounter)
        while (colHeaderCounter < numColHeaders) {
          colHeaderKey = colHeaderValues(colHeaderCounter).toList
          key = (rowHeaderKey, colHeaderKey)
          value = dataForPath.getOrElse(key, NoValue)
          data(pathCounter)(rowHeaderCounter)(colHeaderCounter) = value
          colHeaderCounter += 1
        }
        colHeaderCounter = 0
        pathCounter += 1
      }
      pathCounter = 0
      rowHeaderCounter += 1
    }

    val fieldGroup = tableDataSource.fieldDefinitionGroup.fieldGroup

    TableData(fieldGroup, tableState, result.rowHeaderValues, allColHeaderValues, data, result.valueLookUp)
  }
}

class TableDataGeneratorComparator(fields:Array[Field], fieldDefinitions:Array[FieldDefinition],
                                   lookUps:Array[Array[Any]]) extends Comparator[Array[Int]] {
  require(fieldDefinitions.length == lookUps.length, "Must be the same length")

  private var length = -1
  private var counter = 0
  private var sorted = false
  private var result = 0
  private var lookUp:Array[Any] = _

  def compare(array1:Array[Int], array2:Array[Int]) = {
    length = array1.length
    sorted = false
    counter = 0
    while (!sorted && counter < length) {
      val fieldDefinition = fieldDefinitions(counter)
      lookUp = lookUps(counter)
      if (fields(counter).sortOrder == SortOrder.Ascending) {
        result = fieldDefinition.ordering.compare(
          lookUp(array1(counter)).asInstanceOf[fieldDefinition.T],
          lookUp(array2(counter)).asInstanceOf[fieldDefinition.T]
        )
      } else {
        result = fieldDefinition.ordering.compare(
          lookUp(array2(counter)).asInstanceOf[fieldDefinition.T],
          lookUp(array1(counter)).asInstanceOf[fieldDefinition.T]
        )
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