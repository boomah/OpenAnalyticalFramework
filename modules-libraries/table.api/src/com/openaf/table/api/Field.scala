package com.openaf.table.api

case class Field(name:String)
case class FieldWithSelection(field:Field, selection:Selection)
trait Selection
