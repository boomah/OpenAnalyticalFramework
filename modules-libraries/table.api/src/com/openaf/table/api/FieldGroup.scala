package com.openaf.table.api

case class FieldGroup(group:String, fields:List[Field])
case class FieldGroups(groups:List[FieldGroup])
object FieldGroups {
  val Empty = FieldGroups(Nil)
}
