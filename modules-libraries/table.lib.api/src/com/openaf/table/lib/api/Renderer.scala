package com.openaf.table.lib.api

import collection.mutable.{Set => MSet}

trait Renderer[C,V] {
  def combinedText(combined:C):String
  def valueText(value:V):String
}

case object NullRenderer extends Renderer[Null,Null] {
  def combinedText(combined:Null) = "Null"
  def valueText(value:Null) = "Null"
}

trait MSetCombineRenderer[V] extends Renderer[MSet[V],V] {
  def combinedText(combined:MSet[V]) = combined.map(valueText).mkString(",")
}

case object AnyMSetCombineRenderer extends MSetCombineRenderer[Any] {
  def valueText(value:Any) = value.toString
}

case object StringMSetCombineRenderer extends MSetCombineRenderer[String] {
  def valueText(value:String) = value
}

case object IntRenderer extends Renderer[Int,Int] {
  def combinedText(combined:Int) = valueText(combined)
  def valueText(value:Int) = value.toString
}

trait RendererID
case object DefaultRendererID extends RendererID