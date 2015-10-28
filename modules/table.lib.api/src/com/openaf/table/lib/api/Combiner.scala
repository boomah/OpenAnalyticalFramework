package com.openaf.table.lib.api

import collection.mutable.{Set => MSet}
import collection.mutable

trait Combiner[C,V] {
  def initialCombinedValue:C
  def combine(combinedValue:C, value:V):C

  /**
   * Indicates whether this combiner does mutable combines. A combiner would do this for performance reasons.
   */
  def isMutable:Boolean = false
}

case object NullCombiner extends Combiner[Null,Null] {
  def initialCombinedValue = null
  def combine(combinedValue:Null, value:Null) = null
}

case object AnyCombiner extends Combiner[MSet[Any],Any] {
  def initialCombinedValue = new mutable.HashSet[Any]
  def combine(combinedValue:MSet[Any], value:Any) = {
    combinedValue.add(value)
    combinedValue
  }
}

case object StringCombiner extends Combiner[MSet[String],String] {
  def initialCombinedValue = new mutable.HashSet[String]
  def combine(combinedValue:MSet[String], value:String) = {
    combinedValue += value
    combinedValue
  }
}

case object IntCombiner extends Combiner[Int,Int] {
  def initialCombinedValue = 0
  def combine(combinedValue:Int, value:Int) = combinedValue + value
}

case object MutIntCombiner extends Combiner[MutInt,Integer] {
  def initialCombinedValue = new MutInt(0)
  def combine(combinedValue:MutInt, value:Integer) = {
    combinedValue.value += value.intValue
    combinedValue
  }
  override def isMutable = true

  val One = new Integer(1)
}

sealed trait CombinerType {
  def nameId:String
  def combiner(combiner:Combiner[Int,Int]):Combiner[_,Int]
}

object CombinerType {
  val Types = List(Sum, Average)
}

case object Sum extends CombinerType {
  override def nameId = "combiner.sum"
  override def combiner(combiner:Combiner[Int,Int]) = combiner
}

case object Average extends CombinerType {
  override def nameId = "combiner.average"
  override def combiner(combiner:Combiner[Int,Int]) = AverageCombiner(combiner)
}

case class AverageCombiner(combiner:Combiner[Int,Int]) extends Combiner[Double,Int] {
  private var counter = 0
  private var runningSum = 0
  override def initialCombinedValue = 0.0
  override def combine(combinedValue:Double, value:Int) = {
    counter += 1
    runningSum += value
    runningSum / counter
  }
  override def isMutable = true
}