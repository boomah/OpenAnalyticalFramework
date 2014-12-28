package com.openaf.table.server

import scala.collection.mutable.ArrayBuffer

class IntArraySet(startIndex:Int, endIndex:Int) {
  // TODO - naive implementation of this special set put in so there is something that works rather than something that is efficient
  private val data = new ArrayBuffer[Array[Int]]

  def contains(array:Array[Int]):Boolean = {
    data.exists(dataArray => arrayEquals(dataArray, array))
  }

  def +=(array:Array[Int]) {
    if (!contains(array)) {
      data += array
    }
  }

  def values:Array[Array[Int]] = {
    data.toArray
  }

  private def arrayHashCode(array:Array[Int]) = {
    var result = 1
    var counter = startIndex
    while (counter < endIndex) {
      val element = array(counter)
      val elementHash = element ^ (element >>> 32)
      result = 31 * result + elementHash
      counter += 1
    }
    result
  }

  private def arrayEquals(array:Array[Int], other:Array[Int]) = {
    var result = true
    var counter = startIndex
    while (result && counter < endIndex) {
      result = array(counter) == other(counter)
      counter += 1
    }
    result
  }
}
