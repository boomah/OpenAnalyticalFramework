package com.openaf.table.lib.api

trait Renderer {
  def text(value:Any):String
}

case object StringRenderer extends Renderer {
  def text(value:Any) = value.asInstanceOf[String]
}


