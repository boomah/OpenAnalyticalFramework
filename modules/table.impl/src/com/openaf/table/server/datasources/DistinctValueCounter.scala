package com.openaf.table.server.datasources

/**
 * Structure that assigns an ascending Int to distinct values and generates an Array with these distinct values in the
 * matching position.
 */
class DistinctValueCounter {
  // Always need the table length to be a power of 2.
  private var tableLength = 512
  private var tableLengthM1 = tableLength - 1
  private var threshold = tableLength >> 2
  private var valueTable = new Array[Any](tableLength)
  private var intTable = new Array[Int](tableLength)
  private var counter = 0

  final def intForValue(value:Any):Int = {
    var index = value.hashCode & tableLengthM1
    var entry = valueTable(index)
    while (entry != null) {
      if (entry == value) return intTable(index)
      index = (index + 1) & tableLengthM1
      entry = valueTable(index)
    }
    valueTable(index) = value
    val result = counter
    intTable(index) = result
    counter += 1
    if (counter > threshold) growTables()
    result
  }

  @inline private final def addExistingEntry(existingEntry:Any, existingInt:Int):Unit = {
    var index = existingEntry.hashCode & tableLengthM1
    var entry = valueTable(index)
    while (entry != null) {
      index = (index + 1) & tableLengthM1
      entry = valueTable(index)
    }
    valueTable(index) = existingEntry
    intTable(index) = existingInt
  }

  @inline private final def growTables():Unit = {
    val existingValueTable = valueTable
    val existingIntTable = intTable
    tableLength = tableLength << 1
    tableLengthM1 = tableLength - 1
    threshold = tableLength >> 2
    valueTable = new Array[Any](tableLength)
    intTable = new Array[Int](tableLength)
    var index = 0
    while (index < existingValueTable.length) {
      val entry = existingValueTable(index)
      if (entry != null) addExistingEntry(entry, existingIntTable(index))
      index += 1
    }
  }

  final def toArray:Array[Any] = {
    val result = new Array[Any](counter)
    var index = 0
    while (index < tableLength) {
      val entry = valueTable(index)
      if (entry != null) {
        result(intTable(index)) = entry
      }
      index += 1
    }
    result
  }
}
