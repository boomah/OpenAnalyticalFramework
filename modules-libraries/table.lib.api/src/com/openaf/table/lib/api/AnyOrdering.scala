package com.openaf.table.lib.api

case object AnyOrdering extends Ordering[Any] {
  def compare(anyX:Any,anyY:Any) = {
    (anyX,anyY) match {
      case (x:Int,y:Int) => Integer.compare(x,y)
      case (x:String,y:String) => x.compareTo(y)
      case _ => 0
    }
  }
}
