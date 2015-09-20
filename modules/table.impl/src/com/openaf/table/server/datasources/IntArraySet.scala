package com.openaf.table.server.datasources

/**
 * Set like data structure that provides a distinct array of Int arrays.
 */
class IntArraySet {
  private var tableLength = 400
  private var used = 0
  private var threshold = tableLength >> 2
  private var table = new Array[Array[Int]](tableLength)

  @inline
  private final def hash(array:Array[Int]) = {
    var hash = 1
    var index = array.length - 1
    while (index >= 0) {
      hash = 31 * hash + array(index)
      index -= 1
    }
    hash
  }

  @inline
  private final def arraysEqual(array1:Array[Int], array2:Array[Int]) = {
    var equal = true
    var index = array1.length - 1
    while (index >= 0 && equal) {
      equal = array1(index) == array2(index)
      index -= 1
    }
    equal
  }

  @inline
  private final def tableIndex(hash:Int) = hash % tableLength

  final def +=(array:Array[Int]):Unit = {
    var index = tableIndex(hash(array))
    var entry = table(index)
    while (entry ne null) {
      if (arraysEqual(entry, array)) return
      index = tableIndex(index + 1)
      entry = table(index)
    }
    table(index) = array
    used += 1
    if (used > threshold) growTable()
  }

  @inline
  private final def addOldEntry(array:Array[Int]):Unit = {
    var index = tableIndex(hash(array))
    var entry = table(index)
    while (entry ne null) {
      index = tableIndex(index + 1)
      entry = table(index)
    }
    table(index) = array
  }

  @inline
  private final def growTable():Unit = {
    val oldTable = table
    tableLength = tableLength << 1
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

