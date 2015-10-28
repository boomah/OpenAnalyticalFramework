package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.Field

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
  val defaultField = Field.Null
  val primaryKey = false
  val ordering = NullOrdering
  val combiner = NullCombiner
}

class AnyFieldDefinition(val defaultField:Field[Any]) extends FieldDefinition {
  type V = Any
  type C = Set[Any]
  val primaryKey = false
  val ordering = AnyOrdering
  def combiner = new AnyCombiner
}

object AnyFieldDefinition {
  def apply(defaultField:Field[Any]) = new AnyFieldDefinition(defaultField)
}

class StringFieldDefinition(val defaultField:Field[String]) extends FieldDefinition {
  type V = String
  type C = Set[String]
  val primaryKey = false
  val ordering = StringOrdering
  def combiner = new StringCombiner
}

object StringFieldDefinition {
  def apply(defaultField:Field[String]) = new StringFieldDefinition(defaultField)
}

class IntFieldDefinition(val defaultField:Field[Int]) extends FieldDefinition {
  type V = Int
  type C = Int
  val primaryKey = false
  val ordering = IntOrdering
  def combiner = new IntCombiner
}

object IntFieldDefinition {
  def apply(defaultField:Field[Int]) = new IntFieldDefinition(defaultField)
}

class IncrementingFieldDefinition(val defaultField:Field[Integer]) extends FieldDefinition {
  type V = Integer
  type C = Int
  val primaryKey = false
  val ordering = IntegerOrdering
  def combiner = new IntegerCombiner
}

