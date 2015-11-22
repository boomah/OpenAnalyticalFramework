package com.openaf.table.server

import java.util.Comparator
import com.openaf.table.lib.api.{NoValue, TableValues, Field}
import TableValues._

class RowHeaderComparator(fields:Array[Field[_]], fieldDefinitions:Array[FieldDefinition],
                          lookUps:Array[Array[Any]]) extends Comparator[Array[Int]] {
  private var length = -1
  private var counter = 0
  private var sorted = false
  private var result = 0
  private var lookUp:Array[Any] = _
  private var value1 = -1
  private var value2 = -1

  def compare(array1:Array[Int], array2:Array[Int]):Int = {
    length = array1.length
    sorted = false
    counter = 0
    while (!sorted && counter < length) {
      value1 = array1(counter)
      value2 = array2(counter)
      result = if (value1 != value2) {
        if (value1 == TotalTopInt || value2 == TotalBottomInt) {
          -1
        } else if (value2 == TotalTopInt || value1 == TotalBottomInt) {
          1
        } else {
          val fieldDefinition = fieldDefinitions(counter)
          lookUp = lookUps(counter)
          val lookedUpValue1 = lookUp(value1).asInstanceOf[fieldDefinition.V]
          val lookedUpValue2 = lookUp(value2).asInstanceOf[fieldDefinition.V]
          val sortOrder = fields(counter).sortOrder
          if (lookedUpValue1 == NoValue) {
            sortOrder.direction
          } else if (lookedUpValue2 == NoValue) {
            -sortOrder.direction
          } else {
            fieldDefinition.ordering.compare(lookedUpValue1, lookedUpValue2) * sortOrder.direction
          }
        }
      } else {
        0
      }
      if (result != 0) {
        sorted = true
      } else {
        counter += 1
      }
    }
    result
  }
}
