package com.openaf.table.server

import com.openaf.table.lib.api.{FieldID, FieldGroup}
import collection.immutable.Seq

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

case class FieldDefinitionGroups(fieldDefinitionGroups:Seq[FieldDefinitionGroup]) {
  def rootGroup = FieldDefinitionGroup("root", fieldDefinitionGroups.map(group => Left(group)).toList)
  def fieldGroup = rootGroup.fieldGroup
  def fieldDefinition(fieldID:FieldID) = fieldDefinitionGroups.flatMap(_.fieldDefinitions).find(_.fieldID == fieldID).get
}