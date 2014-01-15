package com.openaf.table.lib.api

import SortOrder._

case class Field[T](id:FieldID, fieldType:FieldType=Dimension, filter:Filter[T]=NoFilter[T](),
                    sortOrder:SortOrder=Ascending) {
  def displayName = id.id
  def flipSortOrder = copy(sortOrder = if (sortOrder == Ascending) Descending else Ascending)
  def updateFilter(filter:Filter[T]) = copy(filter = filter)
}

object Field {
  def apply[T](id:String) = new Field[T](FieldID(id))
  def apply[T](id:String, fieldType:FieldType) = new Field[T](FieldID(id), fieldType)
}

case class FieldID(id:String)

sealed trait FieldType {
  def isDimension:Boolean
  def isMeasure:Boolean
}
case object Dimension extends FieldType {
  def isDimension = true
  def isMeasure = false
}
case object Measure extends FieldType {
  def isDimension = false
  def isMeasure = true
}
case class MultipleFieldType(currentFieldType:FieldType) extends FieldType {
  def isDimension = currentFieldType.isDimension
  def isMeasure = currentFieldType.isMeasure
}

sealed trait Filter[T] {
  def matches(value:T):Boolean
}
case class NoFilter[T]() extends Filter[T] {
  def matches(value:T) = true
}
case class SpecifiedFilter[T](values:Set[T]) extends Filter[T] {
  def matches(value:T) = values.contains(value)
}