package com.openaf.table.lib.api

import java.util.{Set => JSet, HashSet => JHashSet}

trait Combiner[C,V] {
  def initialCombinedValue:C
  def combine(combinedValue:C, value:V):C
}

case object NullCombiner extends Combiner[Null,Null] {
  def initialCombinedValue = null
  def combine(combinedValue:Null, value:Null) = null
}

case object AnyCombiner extends Combiner[JSet[Any],Any] {
  def initialCombinedValue = new JHashSet[Any]
  def combine(combinedValue:JSet[Any], value:Any) = {
    combinedValue.add(value)
    combinedValue
  }
}

case object IntCombiner extends Combiner[Int,Int] {
  def initialCombinedValue = 0
  def combine(combinedValue:Int, value:Int) = combinedValue + value
}
