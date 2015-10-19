package com.openaf.table.gui

import com.openaf.table.lib.api.{NoValue, MutInt, FieldID}
import com.openaf.table.lib.api.StandardFields._

import collection.mutable.{Set => MSet}

trait Renderer[V] {
  def render(value:V):String
}

case object BlankRenderer extends Renderer[Any] {
  def render(value:Any) = ""
}

case class NoValueAwareDelegatingRenderer[V](renderer:Renderer[V]) extends Renderer[Any] {
  def render(value:Any) = if (value == NoValue) "" else renderer.render(value.asInstanceOf[V])
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

case object IntegerRenderer extends Renderer[Integer] {
  def render(value:Integer) = value.toString
}

case object MutIntRenderer extends Renderer[MutInt] {
  def render(value:MutInt) = value.value.toString
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

object Renderer {
  val StandardRenderers:Map[FieldID,Renderer[_]] = Map(
    CountField.id -> MutIntRenderer
  )
}
