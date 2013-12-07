package com.openaf.table.lib.api

import SortOrder._

case class Field(id:FieldID, fieldType:FieldType=Dimension, filter:Filter=AllSelection, sortOrder:SortOrder=Ascending) {
  def displayName = id.id
  def flipSortOrder = copy(sortOrder = if (sortOrder == Ascending) Descending else Ascending)
  def updateFilter(filter:Filter) = copy(filter = filter)
}

object Field {
  def apply(id:String) = new Field(FieldID(id))
  def apply(id:String, fieldType:FieldType) = new Field(FieldID(id), fieldType)
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

sealed trait Filter
case object AllSelection extends Filter
