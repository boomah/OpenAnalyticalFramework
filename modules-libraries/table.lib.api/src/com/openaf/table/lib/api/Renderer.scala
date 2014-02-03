package com.openaf.table.lib.api

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

trait RendererID
case object DefaultRendererID extends RendererID