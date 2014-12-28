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

case object NullOrdering extends Ordering[Null] {
  def compare(anyX:Null,anyY:Null) = 0
}

case object StringOrdering extends Ordering[String] {
  def compare(stringX:String,stringY:String) = stringX.compareTo(stringY)
}

case object IntOrdering extends Ordering[Int] {
  def compare(intX:Int,intY:Int) = Integer.compare(intX, intY)
}

case object IntegerOrdering extends Ordering[Integer] {
  def compare(intX:Integer,intY:Integer) = Integer.compare(intX.intValue, intY.intValue)
}