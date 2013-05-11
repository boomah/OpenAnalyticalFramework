package com.openaf.table.api

case class Field(id:String, fieldType:FieldType=Dimension) {
  def displayName = id
}

sealed trait FieldType
case object Dimension extends FieldType
case object Measure extends FieldType
case class MultipleFieldType(currentFieldType:FieldType) extends FieldType

sealed trait Selection
case object AllSelection extends Selection
