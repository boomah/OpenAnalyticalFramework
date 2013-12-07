package com.openaf.table.lib.api

import SortOrder._

case class Field(id:String, fieldType:FieldType=Dimension, sortOrder:SortOrder=Ascending) {
  def displayName = id
  def flipSortOrder = {copy(sortOrder = if (sortOrder == Ascending) Descending else Ascending)}
}

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

sealed trait Selection
case object AllSelection extends Selection
