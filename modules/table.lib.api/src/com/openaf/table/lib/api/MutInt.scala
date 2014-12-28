package com.openaf.table.lib.api

class MutInt(var value:Int) extends Serializable {
  override def hashCode = Integer.hashCode(value)
  override def equals(other:Any) = {
    other match {
      case mutInt:MutInt => value == mutInt.value
      case _ => false
    }
  }
  override def toString = s"MutInt($value)"
}
