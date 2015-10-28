package com.openaf.table.lib.api

import SortOrder._

case class Field[T](id:FieldID, fieldType:FieldType=Dimension, filter:Filter[T]=RetainAllFilter[T](),
                    sortOrder:SortOrder=Ascending, totals:Totals=Totals.Default, combinerType:CombinerType=Sum,
                    key:FieldKey=NoFieldKey, rendererId:RendererId.RendererId=RendererId.DefaultRendererId) {
  def totalTextID = "total"
  def withSingleFilter(value:T) = copy(filter = RetainFilter[T](Set(value)))
  def withFilter(filter:Filter[T]) = copy(filter = filter)
  def flipSortOrder = copy(sortOrder = if (sortOrder == Ascending) Descending else Ascending)
  def withTotals(totals:Totals) = copy(totals = totals)
  def withKey(key:FieldKey) = copy(key = key)
  def withRendererId(id:RendererId.RendererId) = copy(rendererId = id)
  def withDefaultRendererId = copy(rendererId = RendererId.DefaultRendererId)
  def withCombinerType(combinerType:CombinerType) = copy(combinerType = combinerType)
//  override def toString = id.id
}

object Field {
  val Null = Field[Null](FieldID.Null)
  def apply[T](id:String) = new Field[T](FieldID(id))
  def apply[T](id:String, fieldType:FieldType) = new Field[T](FieldID(id), fieldType)
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

sealed trait FieldKey {
  def number:Int
}
case object NoFieldKey extends FieldKey {def number = 0}
case class RowHeaderFieldKey(number:Int) extends FieldKey
case class ColumnHeaderFieldKey(number:Int) extends FieldKey
case class FilterFieldKey(number:Int) extends FieldKey

object RendererId {
  type RendererId = String
  val DefaultRendererId:RendererId = "com.openaf.table.gui.DefaultRenderer"
}