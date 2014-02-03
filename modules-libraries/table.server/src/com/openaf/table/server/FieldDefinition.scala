package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.Field
import collection.mutable.{Set => MSet}

trait FieldDefinition {
  type V
  type C
  def defaultField:Field[V]
  def fieldID = defaultField.id
  def primaryKey:Boolean
  def renderer:Renderer[V]
  def ordering:Ordering[V]
  def combiner:Combiner[C,V]
}

case object NullFieldDefinition extends FieldDefinition {
  type V = Null
  type C = Null
  def defaultField = Field.Null
  def primaryKey = false
  def renderer = NullRenderer
  def ordering = NullOrdering
  def combiner = NullCombiner
}

case class AnyFieldDefinition(defaultField:Field[Any]) extends FieldDefinition {
  type V = Any
  type C = MSet[Any]
  val primaryKey = false
  val renderer = AnyRenderer
  val ordering = AnyOrdering
  val combiner = AnyCombiner
}

case class StringFieldDefinition(defaultField:Field[String]) extends FieldDefinition {
  type V = String
  type C = MSet[String]
  val primaryKey = false
  val renderer = StringRenderer
  val ordering = StringOrdering
  val combiner = StringCombiner
}

case class IntFieldDefinition(defaultField:Field[Int]) extends FieldDefinition {
  type V = Int
  type C = Int
  def primaryKey = false
  def renderer = IntRenderer
  def ordering = IntOrdering
  def combiner = IntCombiner
}