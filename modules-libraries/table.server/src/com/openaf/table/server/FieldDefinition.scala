package com.openaf.table.server

import com.openaf.table.api.{FieldGroup, Field}

class FieldDefinition(val field:Field)

case class FieldDefinitionGroup(groupName:String, children:List[Either[FieldDefinitionGroup,FieldDefinition]]) {
  def fieldGroup:FieldGroup = {
    val convertedChildren = children.map(fieldDefinitionGroupOrFieldDefinition => {
      fieldDefinitionGroupOrFieldDefinition match {
        case Left(fieldDefinitionGroup) => Left(fieldDefinitionGroup.fieldGroup)
        case Right(fieldDefinition) => Right(fieldDefinition.field)
      }
    })
    FieldGroup(groupName, convertedChildren)
  }
}
object FieldDefinitionGroup {
  val Empty = FieldDefinitionGroup("Fields", Nil)
}
