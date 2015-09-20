package com.openaf.table.server.datasources

import java.util

class ColumnHeaderPath(val fieldsPathIndex:Int, val values:Array[Int]) {
  override val hashCode = fieldsPathIndex + 31 * util.Arrays.hashCode(values)
  override def equals(other:Any) = {
    val otherColumnHeaderPath = other.asInstanceOf[ColumnHeaderPath]
    (fieldsPathIndex == otherColumnHeaderPath.fieldsPathIndex) && util.Arrays.equals(values, otherColumnHeaderPath.values)
  }
  override def toString = s"ColumnHeaderPath($fieldsPathIndex,${values.toList.mkString("[",",","]")})"
}

class DataPath(val rowHeaderValues:Array[Int], val columnHeaderPath:ColumnHeaderPath) {
  override val hashCode = 31 * util.Arrays.hashCode(rowHeaderValues) + 31 * columnHeaderPath.hashCode
  override def equals(other:Any) = {
    val otherDataPath = other.asInstanceOf[DataPath]
    util.Arrays.equals(rowHeaderValues, otherDataPath.rowHeaderValues) && (columnHeaderPath == otherDataPath.columnHeaderPath)
  }
  override def toString = s"DataPath(${rowHeaderValues.toList.mkString("[",",","]")},$columnHeaderPath)"
}