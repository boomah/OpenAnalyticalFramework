package com.openaf.table.server.datasources

/**
 * Set like data structure that provides a distinct array of Int arrays.
 */
class IntArraySet(width:Int) {
  // Always need the table length to be a power of 2.
  private var tableLength = 512
  private var tableLengthM1 = tableLength - 1
  private var threshold = tableLength >> 2
  private var table = new Array[Array[Int]](tableLength)
  private var used = 0
  private val widthM1 = width - 1

  @inline private final def hash(array:Array[Int]) = {
    var hash = 1
    var index = widthM1
    while (index >= 0) {
      hash = 31 * hash + array(index)
      index -= 1
    }
    hash
  }

  @inline private final def arraysEqual(array1:Array[Int], array2:Array[Int]):Boolean = {
    var index = widthM1
    while (index >= 0) {
      if (array1(index) != array2(index)) return false
      index -= 1
    }
    true
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

  @inline private final def addOldEntry(array:Array[Int]):Unit = {
    var index = hash(array) & tableLengthM1
    var entry = table(index)
    while (entry ne null) {
      index = (index + 1) & tableLengthM1
      entry = table(index)
    }
    table(index) = array
  }

  @inline private final def growTable():Unit = {
    val oldTable = table
    tableLength = tableLength << 1
    tableLengthM1 = tableLength - 1
    threshold = tableLength >> 2
    table = new Array[Array[Int]](tableLength)
    var oldTableIndex = 0
    while (oldTableIndex < oldTable.length) {
      val entry = oldTable(oldTableIndex)
      if (entry ne null) addOldEntry(entry)
      oldTableIndex += 1
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
