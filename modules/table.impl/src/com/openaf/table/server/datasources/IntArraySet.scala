package com.openaf.table.server.datasources

/**
 * Set like data structure that provides a distinct array of Int arrays.
 */
class IntArraySet {
  // Always need the table length to be a power of 2.
  private var tableLength = 512
  private var tableLengthM1 = tableLength - 1
  private var threshold = tableLength >> 2
  private var table = new Array[Array[Int]](tableLength)
  private var used = 0

  @inline private final def hash(array:Array[Int]) = {
    var hash = 1
    var index = array.length - 1
    while (index >= 0) {
      hash = 31 * hash + array(index)
      index -= 1
    }
    hash
  }

  @inline private final def arraysEqual(array1:Array[Int], array2:Array[Int]):Boolean = {
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

  final def +=(array:Array[Int]):Unit = {
    var index = hash(array) & tableLengthM1
    var entry = table(index)
    while (entry ne null) {
      if (arraysEqual(entry, array)) return
      index = (index + 1) & tableLengthM1
      entry = table(index)
    }
    table(index) = array
    used += 1
    if (used > threshold) growTable()
  }

  @inline private final def addExistingEntry(existingEntry:Array[Int]):Unit = {
    var index = hash(existingEntry) & tableLengthM1
    var entry = table(index)
    while (entry ne null) {
      index = (index + 1) & tableLengthM1
      entry = table(index)
    }
    table(index) = existingEntry
  }

  @inline private final def growTable():Unit = {
    val existingTable = table
    tableLength = tableLength << 1
    tableLengthM1 = tableLength - 1
    threshold = tableLength >> 2
    table = new Array[Array[Int]](tableLength)
    var index = 0
    while (index < existingTable.length) {
      val entry = existingTable(index)
      if (entry ne null) addExistingEntry(entry)
      index += 1
    }
  }

  final def toArray:Array[Array[Int]] = {
    val result = new Array[Array[Int]](used)
    var tableIndex = 0
    var resultIndex = 0
    while (tableIndex < tableLength) {
      val entry = table(tableIndex)
      if (entry ne null) {
        result(resultIndex) = entry
        resultIndex += 1
      }
      tableIndex += 1
    }
    result
  }
}
