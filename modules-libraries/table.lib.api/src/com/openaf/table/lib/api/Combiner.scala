package com.openaf.table.lib.api

import collection.mutable.{Set => MSet}
import collection.mutable

trait Combiner[C,V] {
  def initialCombinedValue:C
  def combine(combinedValue:C, value:V):C
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
