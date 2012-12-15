package com.openaf.table.api

case class Field(id:String, displayName:String) {
  def this(id:String) = this(id, id)
  override def toString = "Field(" + id + ")"
}
case class FieldWithSelection(field:Field, selection:Selection)
trait Selection
