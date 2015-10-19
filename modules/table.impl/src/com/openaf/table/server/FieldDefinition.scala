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
  def ordering:Ordering[V]
  def combiner:Combiner[C,V]
}

case object NullFieldDefinition extends FieldDefinition {
  type V = Null
  type C = Null
  def defaultField = Field.Null
  def primaryKey = false
  def ordering = NullOrdering
  def combiner = NullCombiner
}

class AnyFieldDefinition(val defaultField:Field[Any]) extends FieldDefinition {
  type V = Any
  type C = MSet[Any]
  val primaryKey = false
  val ordering = AnyOrdering
  val combiner = AnyCombiner
}

object AnyFieldDefinition {
  def apply(defaultField:Field[Any]) = new AnyFieldDefinition(defaultField)
}

class StringFieldDefinition(val defaultField:Field[String]) extends FieldDefinition {
  type V = String
  type C = MSet[String]
  val primaryKey = false
  val ordering = StringOrdering
  val combiner = StringCombiner
}

object StringFieldDefinition {
  def apply(defaultField:Field[String]) = new StringFieldDefinition(defaultField)
}

class IntFieldDefinition(val defaultField:Field[Int]) extends FieldDefinition {
  type V = Int
  type C = Int
  def primaryKey = false
  def ordering = IntOrdering
  def combiner = IntCombiner
}

object IntFieldDefinition {
  def apply(defaultField:Field[Int]) = new IntFieldDefinition(defaultField)
}

class IncrementingFieldDefinition(val defaultField:Field[Integer]) extends FieldDefinition {
  type V = Integer
  type C = MutInt
  def primaryKey = false
  def ordering = IntegerOrdering
  def combiner = MutIntCombiner
}

