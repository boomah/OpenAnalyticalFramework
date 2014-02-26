package com.openaf.table.server

import com.openaf.table.lib.api.{FieldID, FieldGroup}

case class FieldDefinitionGroup(groupName:String, children:List[Either[FieldDefinitionGroup,FieldDefinition]]) {
  def fieldGroup:FieldGroup = {
    val convertedChildren = children.map{
      case Left(fieldDefinitionGroup) => Left(fieldDefinitionGroup.fieldGroup)
      case Right(fieldDefinition) => Right(fieldDefinition.defaultField)
    }
    FieldGroup(groupName, convertedChildren)
  }

  def fieldDefinitions:List[FieldDefinition] = {
    children.flatMap {
      case Left(childFieldDefinitionGroup) => childFieldDefinitionGroup.fieldDefinitions
      case Right(childFieldDefinition) => List(childFieldDefinition)
    }
  }

  def fieldDefinition(fieldID:FieldID) = fieldDefinitions.find(_.fieldID == fieldID).get
}
object FieldDefinitionGroup {
  val Empty = FieldDefinitionGroup("Fields", Nil)

  val Standard = FieldDefinitionGroup("Standard", StandardFieldDefinitions.All.map(Right(_)))
}
