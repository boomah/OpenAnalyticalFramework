package com.openaf.table.api

case class Field(id:String) {
  def displayName = id
}
case class FieldWithSelection(field:Field, selection:Selection)
trait Selection
case object AllSelection extends Selection
