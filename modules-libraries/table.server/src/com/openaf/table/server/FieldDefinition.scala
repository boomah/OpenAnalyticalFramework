package com.openaf.table.server

import com.openaf.table.api.{FieldGroups, FieldGroup, Field}

class FieldDefinition(val field:Field)

case class FieldDefinitionGroup(group:String, definitions:List[FieldDefinition]) {
  def fieldGroup = FieldGroup(group, definitions.map(_.field))
}
case class FieldDefinitionGroups(groups:List[FieldDefinitionGroup]) {
  def fieldGroups = FieldGroups(groups.map(_.fieldGroup))
}
object FieldDefinitionGroups {
  val Empty = FieldDefinitionGroups(Nil)
}
