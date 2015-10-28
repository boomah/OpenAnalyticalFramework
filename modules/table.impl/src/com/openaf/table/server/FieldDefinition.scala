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

  /**
   * Must always return a new Combiner here.
   */
  def combiner:Combiner[C,V]

  /**
   * Returns the combiner appropriate for the supplied CombinerType. Ideally this could be done with just the Combiner
   * using Numeric for primitive types but implementers should override it and provide their own Combiners for
   * performance reasons.
   */
  def combinerFromType(combinerType:CombinerType):Combiner[C,V] = {
    combinerType match {
      case Sum => combiner
      case other => throw new IllegalStateException(s"No Combiner specified for $other")
    }
  }
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
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Average => new AverageIntCombiner
    }
  }
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
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Average => new AverageIntegerCombiner
    }
  }
}

