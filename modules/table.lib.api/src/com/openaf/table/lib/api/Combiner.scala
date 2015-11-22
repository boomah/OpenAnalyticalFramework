package com.openaf.table.lib.api

import scala.collection.mutable
import java.lang.{Double => JDouble}

trait Combiner[C,V] {
  def combine(value:V):Unit
  def value:C
}

case object NullCombiner extends Combiner[Null,Null] {
  override def combine(value:Null) = {}
  override def value = null
}

class AnyCombiner extends Combiner[Set[Any],Any] {
  private val set = new mutable.HashSet[Any]
  override def combine(value:Any) = {set.add(value)}
  override def value = set.toSet
}

class StringCombiner extends Combiner[Set[String],String] {
  private val set = new mutable.HashSet[String]
  override def combine(value:String) = {set += value}
  override def value = set.toSet
}

class IntCombiner extends Combiner[Int,Int] {
  private var count = 0
  override def combine(value:Int) = {count += value}
  override def value = count
}

class IntegerCombiner extends Combiner[Int,Integer] {
  private var count = 0
  def combine(value:Integer) = {count += value.intValue}
  override def value = count
}

object IntegerCombiner {
  val One = Integer.valueOf(1)
}

class MeanIntCombiner extends Combiner[Int,Int] {
  private var counter = 0
  private var runningSum = 0
  override def combine(value:Int) = {
    counter += 1
    runningSum += value
  }
  override def value = runningSum / counter
}

class MeanIntegerCombiner extends Combiner[Int,Integer] {
  private var counter = 0
  private var runningSum = 0
  def combine(value:Integer) = {
    counter += 1
    runningSum += value.intValue
  }
  // TODO - make sure this can't be divided by zero in the same way as the duration mean combiner
  override def value = runningSum / counter
}

class MinIntCombiner extends Combiner[Int,Int] {
  private var min = Int.MaxValue
  override def combine(value:Int) = {
    if (value < min) {min = value}
  }
  override def value = min
}

class MinIntegerCombiner extends Combiner[Int,Integer] {
  private var min = Int.MaxValue
  override def combine(value:Integer) = {
    if (value.intValue < min) {min = value.intValue}
  }
  override def value = min
}

class MaxIntCombiner extends Combiner[Int,Int] {
  private var max = Int.MinValue
  override def combine(value:Int) = {
    if (value > max) {max = value}
  }
  override def value = max
}

class MaxIntegerCombiner extends Combiner[Int,Integer] {
  private var max = Int.MinValue
  override def combine(value:Integer) = {
    if (value.intValue > max) {max = value.intValue}
  }
  override def value = max
}

/**
 * A combiner that always returns 1. This can be used when the value of max, min, mean and median is always going to be
 * 1 such as for a count field.
 */
object OneIntegerCombiner extends Combiner[Int,Integer] {
  def combine(value:Integer) = {}
  override def value = 1
}

class DoubleCombiner extends Combiner[Double,Double] {
  private var count = 0.0
  override def combine(value:Double) = {count += value}
  override def value = count
}

class JDoubleCombiner extends Combiner[Double,JDouble] {
  private var count = 0.0
  def combine(value:JDouble) = {count += value.doubleValue}
  override def value = count
}