package com.openaf.table.lib.api

import scala.collection.mutable

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

class AverageIntCombiner extends Combiner[Int,Int] {
  private var counter = 0
  private var runningSum = 0
  override def combine(value:Int) = {
    counter += 1
    runningSum += value
  }
  override def value = runningSum / counter
}

class AverageIntegerCombiner extends Combiner[Int,Integer] {
  private var counter = 0
  private var runningSum = 0
  def combine(value:Integer) = {
    counter += 1
    runningSum += value.intValue
  }
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