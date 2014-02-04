package com.openaf.table.lib.api

import collection.mutable.{Set => MSet}

trait Renderer[V] {
  def render(value:V):String
}

case object NullRenderer extends Renderer[Null] {
  def render(value:Null) = "Null"
}

case object AnyRenderer extends Renderer[Any] {
  def render(value:Any) = value.toString
}

case object StringRenderer extends Renderer[String] {
  def render(value:String) = value
}

case object IntRenderer extends Renderer[Int] {
  def render(value:Int) = value.toString
}

case class MSetRenderer[T](maxNumberToDisplay:Int) extends Renderer[MSet[T]] {
  def render(value:MSet[T]) = {
    val numberOfValues = value.size
    if (numberOfValues > maxNumberToDisplay) {
      numberOfValues + " values"
    } else {
      value.mkString(",")
    }
  }
}

object MSetRenderer {
  val DefaultMaxNumberToDisplay = 3
}

trait RendererID
case object DefaultRendererID extends RendererID