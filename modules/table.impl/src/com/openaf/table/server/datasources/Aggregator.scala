package com.openaf.table.server.datasources

import com.openaf.table.lib.api.NoValue
import com.openaf.table.server.FieldDefinition

/**
 * Map like structure that efficiently aggregates values based on a key consisting of two Int arrays.
 */
class Aggregator(rowHeaderWidth:Int) {
  // Always need the table length to be a power of 2.
  private var tableLength = 512
  private var tableLengthM1 = tableLength - 1
  private var threshold = tableLength >> 2
  private var rowHeaderTable = new Array[Array[Int]](tableLength)
  private var columnHeaderTable = new Array[Array[Int]](tableLength)
  private var valueTable = new Array[Any](tableLength)
  private var used = 0
  private val rowHeaderWidthM1 = rowHeaderWidth - 1

  @inline private final def hash(rowHeaders:Array[Int], columnHeaders:Array[Int]) = {
    var hash = 1
    var index = rowHeaderWidthM1
    while (index >= 0) {
      hash = 31 * hash + rowHeaders(index)
      index -= 1
    }
    index = columnHeaders.length - 1
    while (index >= 0) {
      hash = 31 * hash + columnHeaders(index)
      index -= 1
    }
    hash
  }

  @inline private final def rowArraysEqual(array1:Array[Int], array2:Array[Int]):Boolean = {
    var index = rowHeaderWidthM1
    while (index >= 0) {
      if (array1(index) != array2(index)) return false
      index -= 1
    }
    true
  }

  @inline private final def columnArraysEqual(array1:Array[Int], array2:Array[Int]):Boolean = {
    val length = array1.length
    if (length == array2.length) {
      var index = length - 1
      while (index >= 0) {
        if (array1(index) != array2(index)) return false
        index -= 1
      }
      true
    } else {
      false
    }
  }

  final def combine(value:Any, fieldDefinition:FieldDefinition, rowHeaders:Array[Int], columnHeaders:Array[Int]):Unit = {
    val combiner = fieldDefinition.combiner
    var index = hash(rowHeaders, columnHeaders) & tableLengthM1
    var rowHeaderEntry = rowHeaderTable(index)
    while (rowHeaderEntry ne null) {
      if (rowArraysEqual(rowHeaderEntry, rowHeaders) && columnArraysEqual(columnHeaderTable(index), columnHeaders)) {
        // Data already there, aggregate it.
        val newDataValue = combiner.combine(
          valueTable(index).asInstanceOf[fieldDefinition.C],
          value.asInstanceOf[fieldDefinition.V]
        )
        if (!combiner.isMutable) {valueTable(index) = newDataValue}
        return
      }
      index = (index + 1) & tableLengthM1
      rowHeaderEntry = rowHeaderTable(index)
    }

    // Data not found.
    rowHeaderTable(index) = rowHeaders
    columnHeaderTable(index) = columnHeaders
    valueTable(index) = combiner.combine(combiner.initialCombinedValue, value.asInstanceOf[fieldDefinition.V])

    used += 1
    if (used > threshold) growTable()
  }

  @inline private final def addExistingEntry(existingRowHeaderEntry:Array[Int],
                                             existingColumnHeaderEntry:Array[Int], existingValue:Any):Unit = {
    var index = hash(existingRowHeaderEntry, existingColumnHeaderEntry) & tableLengthM1
    var rowHeaderEntry = rowHeaderTable(index)
    while (rowHeaderEntry ne null) {
      index = (index + 1) & tableLengthM1
      rowHeaderEntry = rowHeaderTable(index)
    }
    rowHeaderTable(index) = existingRowHeaderEntry
    columnHeaderTable(index) = existingColumnHeaderEntry
    valueTable(index) = existingValue
  }

  @inline private final def growTable():Unit = {
    val existingRowHeaderTable = rowHeaderTable
    val existingColumnHeaderTable = columnHeaderTable
    val existingValueTable = valueTable

    tableLength = tableLength << 1
    tableLengthM1 = tableLength - 1
    threshold = tableLength >> 2

    rowHeaderTable = new Array[Array[Int]](tableLength)
    columnHeaderTable = new Array[Array[Int]](tableLength)
    valueTable = new Array[Any](tableLength)

    var index = 0
    while (index < existingRowHeaderTable.length) {
      val entry = existingRowHeaderTable(index)
      if (entry ne null) addExistingEntry(entry, existingColumnHeaderTable(index), existingValueTable(index))
      index += 1
    }
  }

  /**
   * Returns the value at the specified rowHeaders and columnHeaders provided.
   */
  final def apply(rowHeaders:Array[Int], columnHeaders:Array[Int]):Any = {
    var index = hash(rowHeaders, columnHeaders) & tableLengthM1
    var rowHeaderEntry = rowHeaderTable(index)
    if (rowHeaderEntry eq null) {
      NoValue
    } else {
      var columnHeaderEntry = columnHeaderTable(index)
      while (!rowArraysEqual(rowHeaderEntry, rowHeaders) && !columnArraysEqual(columnHeaderEntry, columnHeaders)) {
        index = (index + 1) & tableLengthM1
        rowHeaderEntry = rowHeaderTable(index)
        if (rowHeaderEntry eq null) return NoValue
        columnHeaderEntry = columnHeaderTable(index)
      }
      valueTable(index)
    }
  }
}

object Aggregator {
  val Empty = new Aggregator(0)
}
