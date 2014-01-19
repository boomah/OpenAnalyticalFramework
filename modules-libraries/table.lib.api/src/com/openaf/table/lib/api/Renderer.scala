package com.openaf.table.lib.api

trait Renderer[V] {
  def text(value:V):String
}

case object NullRenderer extends Renderer[Null] {
  def text(value:Null) = "Null"
}

case object AnyRenderer extends Renderer[Any] {
  def text(value:Any) = value.toString
}

case object StringRenderer extends Renderer[String] {
  def text(value:String) = value
}

case object IntRenderer extends Renderer[Int] {
  def text(value:Int) = value.toString
}

