package com.openaf.table.api

case class Field(id:String) {
  def displayName = id
}
trait Selection
case object AllSelection extends Selection
