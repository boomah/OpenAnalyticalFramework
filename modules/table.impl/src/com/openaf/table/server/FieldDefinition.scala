package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.Field
import java.lang.{Double => JDouble}

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

  def transformer[T](transformerType:TransformerType[T]):Transformer[V,T] = {
    transformers.find(
      transformer => transformer.transformerType == transformerType
    ).getOrElse(IdentityTransformer).asInstanceOf[Transformer[V,T]]
  }
  def transformers:List[Transformer[_,_]] = Nil

  def parser:Parser[V]
}

case object NullFieldDefinition extends FieldDefinition {
  override type V = Null
  override type C = Null
  override val defaultField = Field.Null
  override val primaryKey = false
  override val ordering = NullOrdering
  override val combiner = NullCombiner
  override val parser = NullParser
}

class AnyFieldDefinition(val defaultField:Field[Any]) extends FieldDefinition {
  override type V = Any
  override type C = Set[Any]
  override val primaryKey = false
  override val ordering = AnyOrdering
  override def combiner = new AnyCombiner
  override val parser = AnyParser
}

object AnyFieldDefinition {
  def apply(defaultField:Field[Any]) = new AnyFieldDefinition(defaultField)
}

class StringFieldDefinition(val defaultField:Field[String]) extends FieldDefinition {
  override type V = String
  override type C = Set[String]
  override val primaryKey = false
  override val ordering = StringOrdering
  override def combiner = new StringCombiner
  override val parser = StringParser
}

object StringFieldDefinition {
  def apply(defaultField:Field[String]) = new StringFieldDefinition(defaultField)
}

class IntFieldDefinition(val defaultField:Field[Int]) extends FieldDefinition {
  override type V = Int
  override type C = Int
  override val primaryKey = false
  override val ordering = IntOrdering
  override def combiner = new IntCombiner
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Mean => new MeanIntCombiner
      case Median => combiner
      case Min => new MinIntCombiner
      case Max => new MaxIntCombiner
    }
  }
  override def transformers = List(IntToDoubleTransformer)
  override val parser = IntParser
}

object IntFieldDefinition {
  def apply(defaultField:Field[Int]) = new IntFieldDefinition(defaultField)
}

class IncrementingFieldDefinition(val defaultField:Field[Integer]) extends FieldDefinition {
  override type V = Integer
  override type C = Int
  override val primaryKey = false
  override val ordering = IntegerOrdering
  override def combiner = new IntegerCombiner
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Mean => OneIntegerCombiner
      case Median => OneIntegerCombiner
      case Min => OneIntegerCombiner
      case Max => OneIntegerCombiner
    }
  }
  override val parser = IntegerParser
}

class IntegerFieldDefinition(val defaultField:Field[Integer]) extends FieldDefinition {
  override type V = Integer
  override type C = Int
  override val primaryKey = false
  override val ordering = IntegerOrdering
  override def combiner = new IntegerCombiner
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Mean => new MeanIntegerCombiner
      case Median => combiner
      case Min => new MinIntegerCombiner
      case Max => new MaxIntegerCombiner
    }
  }
  override def transformers = List(IntegerToJDoubleTransformer)
  override val parser = IntegerParser
}

object IntegerFieldDefinition {
  def apply(defaultField:Field[Integer]) = new IntegerFieldDefinition(defaultField)
}

class DoubleFieldDefinition(val defaultField:Field[Double]) extends FieldDefinition {
  override type V = Double
  override type C = Double
  override val primaryKey = false
  override val ordering = DoubleOrdering
  override def combiner = new DoubleCombiner
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Mean => new MeanDoubleCombiner
      case Median => combiner
      case Min => new MinDoubleCombiner
      case Max => new MaxDoubleCombiner
    }
  }
  override val parser = DoubleParser
}

object DoubleFieldDefinition {
  def apply(defaultField:Field[Double]) = new DoubleFieldDefinition(defaultField)
}

class JDoubleFieldDefinition(val defaultField:Field[JDouble]) extends FieldDefinition {
  override type V = JDouble
  override type C = Double
  override val primaryKey = false
  override val ordering = JDoubleOrdering
  override def combiner = new JDoubleCombiner
  override def combinerFromType(combinerType:CombinerType) = {
    combinerType match {
      case Sum => combiner
      case Mean => new MeanJDoubleCombiner
      case Median => combiner
      case Min => new MinJDoubleCombiner
      case Max => new MaxJDoubleCombiner
    }
  }
  override val parser = JDoubleParser
}

object JDoubleFieldDefinition {
  def apply(defaultField:Field[JDouble]) = new JDoubleFieldDefinition(defaultField)
}
