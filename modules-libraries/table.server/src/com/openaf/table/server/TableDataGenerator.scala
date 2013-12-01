package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.TableDataSource

object TableDataGenerator {
  def tableData(tableState:TableState, tableDataSource:TableDataSource) = {
    val fieldGroup = tableDataSource.fieldDefinitionGroup.fieldGroup
    /*val result = tableDataSource.result(tableState)

    val rowHeaderValues = result.rowHeaderValues
    val rowHeaderValuesLength = rowHeaderValues.length
    var rowCounter = 0

    val measureAreaValues = result.colHeaderValues
    val measureAreaLength = measureAreaValues.length
    var measureAreaCounter = 0
    val measureAreaLengths = measureAreaValues.map(_.length)
    val totalMeasureAreaLength = measureAreaLengths.sum
    var totalMeasureAreaCounter = 0


    val data:Array[Array[Any]] = new Array(rowHeaderValuesLength)

    while (rowCounter < rowHeaderValuesLength) {
      data(rowCounter) = new Array[Any](totalMeasureAreaLength)
      val rowKey = rowHeaderValues(rowCounter).toList
      while (measureAreaCounter < measureAreaLength) {
        val measureAreaSection = measureAreaValues(measureAreaCounter)
        val measureAreaSectionLength = measureAreaSection.length
        var measureAreaSectionCounter = 0
        while (measureAreaSectionCounter < measureAreaSectionLength) {
          val measureAreaKey = measureAreaSection(measureAreaSectionCounter).toList

          val key = (rowKey, measureAreaKey)
          val value = result.data.getOrElse(key, NoValue)
          data(rowCounter)(totalMeasureAreaCounter) = value

          measureAreaSectionCounter += 1
          totalMeasureAreaCounter += 1
        }
        totalMeasureAreaCounter = 0
        measureAreaCounter += 1
      }
      measureAreaCounter = 0
      totalMeasureAreaCounter = 0

      rowCounter += 1
    }

    TableData(fieldGroup, tableState, result.rowHeaderValues, result.colHeaderValues, data, result.valueLookUp)*/

    TableData(fieldGroup, tableState, Array.empty, Array.empty, Array.empty, Map.empty)
  }
}
