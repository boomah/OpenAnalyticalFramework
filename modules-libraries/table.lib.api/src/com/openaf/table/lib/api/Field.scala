package com.openaf.table.lib.api

import SortOrder._

case class Field[T](id:FieldID, fieldType:FieldType=Dimension, filter:Filter[T]=NoFilter[T](),
                    rendererID:RendererID=DefaultRendererID, sortOrder:SortOrder=Ascending, key:Int=Field.nextKey) {
  def flipSortOrder = copy(sortOrder = if (sortOrder == Ascending) Descending else Ascending)
  def withSingleFilter(value:T) = copy(filter = SpecifiedFilter[T](Set(value)))
  def withFilter(filter:Filter[T]) = copy(filter = filter)
  def duplicate:Field[T] = copy(key = Field.nextKey)
}

object Field {
  val Null = Field[Null](FieldID.Null)
  def apply[T](id:String) = new Field[T](FieldID(id))
  def apply[T](id:String, fieldType:FieldType) = new Field[T](FieldID(id), fieldType)
  private var keyCounter = 0
  private[api] def nextKey = {
    keyCounter += 1
    keyCounter
  }
}

case class FieldID(id:String)
object FieldID {
  val Null = FieldID("Null")
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
