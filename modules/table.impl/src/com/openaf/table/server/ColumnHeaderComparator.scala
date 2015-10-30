package com.openaf.table.server

import java.util.Comparator
import com.openaf.table.lib.api.{SortOrder, TableValues, Field}
import TableValues._

class ColumnHeaderComparator(pathIndexToFields:Array[Array[Field[_]]], fieldKeyFieldDefinitions:Array[FieldDefinition],
                             fieldKeyLookUps:Array[Array[Any]]) extends Comparator[Array[Int]] {
  private var length = -1
  private var array1LengthM1 = -1
  private var array2LengthM1 = -1
  private var counter = 0
  private var sorted = false
  private var result = 0
  private var lookUp:Array[Any] = _
  private var value1 = -1
  private var value2 = -1
  private var field1:Field[_] = _
  private var field2:Field[_] = _

  def compare(array1:Array[Int], array2:Array[Int]):Int = {
    // -1 to the length here because the path index is stored at the end of the array
    array1LengthM1 = array1.length - 1
    array2LengthM1 = array2.length - 1
    length = math.min(array1LengthM1, array2LengthM1)
    sorted = false
    counter = 0
    while (!sorted && counter < length) {
      field1 = pathIndexToFields(array1(array1LengthM1))(counter)
      field2 = pathIndexToFields(array2(array2LengthM1))(counter)
      if (field1 == field2) {
        // Fields are the same so just compare the values
        value1 = array1(counter)
        value2 = array2(counter)
        result = if (value1 == value2) {
          0
        } else {
          if (value1 == TotalTopInt || value2 == TotalBottomInt) {
            -1
          } else if (value2 == TotalTopInt || value1 == TotalBottomInt) {
            1
          } else {
            val fieldDefinition = fieldKeyFieldDefinitions(field1.key.number)
            lookUp = fieldKeyLookUps(field1.key.number)
            if (field1.sortOrder == SortOrder.Ascending) {
              fieldDefinition.ordering.compare(
                lookUp(value1).asInstanceOf[fieldDefinition.V],
                lookUp(value2).asInstanceOf[fieldDefinition.V]
              )
            } else {
              fieldDefinition.ordering.compare(
                lookUp(value2).asInstanceOf[fieldDefinition.V],
                lookUp(value1).asInstanceOf[fieldDefinition.V]
              )
            }
          }
        }
        if (result != 0) {
          sorted = true
        } else {
          counter += 1
        }
      } else {
        // Different fields so just use the field key to determine order
        sorted = true
        result = Integer.compare(field1.key.number, field2.key.number)
      }
    }
    result
  }
}

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
          if (fields(counter).sortOrder == SortOrder.Ascending) {
            fieldDefinition.ordering.compare(
              lookUp(value1).asInstanceOf[fieldDefinition.V],
              lookUp(value2).asInstanceOf[fieldDefinition.V]
            )
          } else {
            fieldDefinition.ordering.compare(
              lookUp(value2).asInstanceOf[fieldDefinition.V],
              lookUp(value1).asInstanceOf[fieldDefinition.V]
            )
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
