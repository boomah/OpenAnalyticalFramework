package com.openaf.table.server.datasources

import com.openaf.table.server.FieldDefinition

class Aggregator(rowHeaderWidth:Int, columnHeaderWidth:Int) {
  // Always need the table length to be a power of 2.
  private var tableLength = 512
  private var tableLengthM1 = tableLength - 1
  private var threshold = tableLength >> 2
  private var rowHeaderTable = new Array[Array[Int]](tableLength)
  private var columnHeaderTable = new Array[Array[Int]](tableLength)
  private var valueTable = new Array[Any](tableLength)
  private var used = 0
  private val rowHeaderWidthM1 = rowHeaderWidth - 1
  private val columnHeaderWidthM1 = columnHeaderWidth - 1

  @inline private final def hash(rowHeaders:Array[Int], columnHeaders:Array[Int]) = {
    var hash = 1
    var index = rowHeaderWidthM1
    while (index >= 0) {
      hash = 31 * hash + rowHeaders(index)
      index -= 1
    }
    index = columnHeaderWidthM1
    while (index >= 0) {
      hash = 31 * hash + columnHeaders(index)
      index -= 1
    }
    hash
  }

  @inline private final def arraysEqual(array1:Array[Int], array2:Array[Int], widthM1:Int):Boolean = {
    var index = widthM1
    while (index >= 0) {
      if (array1(index) != array2(index)) return false
      index -= 1
    }
    true
  }

  final def combine(value:Any, fieldDefinition:FieldDefinition, rowHeaders:Array[Int], columnHeaders:Array[Int]):Unit = {
    val combiner = fieldDefinition.combiner
    var index = hash(rowHeaders, columnHeaders) & tableLengthM1
    var rowHeaderEntry = rowHeaderTable(index)
    while (rowHeaderEntry ne null) {
      if (arraysEqual(rowHeaderEntry, rowHeaders, rowHeaderWidthM1) &&
        arraysEqual(columnHeaderTable(index), columnHeaders, columnHeaderWidthM1)) {
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
    val newDataValue = combiner.combine(combiner.initialCombinedValue, value.asInstanceOf[fieldDefinition.V])
    rowHeaderTable(index) = rowHeaders
    columnHeaderTable(index) = columnHeaders
    valueTable(index) = newDataValue

    used += 1
    if (used > threshold) growTable()
  }

  @inline private final def growTable():Unit = {

  }

  /**
   * Returns the value at the specified rowHeaders and columnHeaders provided. The value must exist or this method
   * will never return.
   */
  final def apply(rowHeaders:Array[Int], columnHeaders:Array[Int]):Any = {
    var index = hash(rowHeaders, columnHeaders) & tableLengthM1
    var rowHeaderEntry = rowHeaderTable(index)
    var columnHeaderEntry = columnHeaderTable(index)
    while (!arraysEqual(rowHeaderEntry, rowHeaders, rowHeaderWidthM1) &&
      !arraysEqual(columnHeaderEntry, columnHeaders, columnHeaderWidthM1)) {
      index = (index + 1) & tableLengthM1
      rowHeaderEntry = rowHeaderTable(index)
      columnHeaderEntry = columnHeaderTable(index)
    }
    valueTable(index)
  }
}
