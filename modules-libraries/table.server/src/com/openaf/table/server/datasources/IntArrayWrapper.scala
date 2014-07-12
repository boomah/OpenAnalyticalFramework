package com.openaf.table.server.datasources

import java.util

class IntArrayWrapper(val array:Array[Int]) {
  override val hashCode = util.Arrays.hashCode(array)
  override def equals(other:Any) = util.Arrays.equals(array, other.asInstanceOf[IntArrayWrapper].array)
  override def toString = s"IntArrayWrapper(${array.toList.mkString("[",",","]")})"
}

class IntArrayWrapperKey(val array1:Array[Int], val array2:Array[Int]) {
  override val hashCode = 31 * util.Arrays.hashCode(array1) + 31 * util.Arrays.hashCode(array2)
  override def equals(other:Any) = {
    val otherIntArrayWrapperKey = other.asInstanceOf[IntArrayWrapperKey]
    util.Arrays.equals(array1, otherIntArrayWrapperKey.array1) && util.Arrays.equals(array2, otherIntArrayWrapperKey.array2)
  }
}