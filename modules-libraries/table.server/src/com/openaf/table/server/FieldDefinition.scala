package com.openaf.table.server

import com.openaf.table.lib.api.{StringRenderer, Renderer, FieldGroup, Field}

trait FieldDefinition {
  def field:Field
  def renderer:Renderer
  def primaryKey:Boolean
}

case class DefaultFieldDefinition(field:Field) extends FieldDefinition {
  val renderer = StringRenderer
  val primaryKey = false
}

case class FieldDefinitionGroup(groupName:String, children:List[Either[FieldDefinitionGroup,FieldDefinition]]) {
  def fieldGroup:FieldGroup = {
    val convertedChildren = children.map{
      case Left(fieldDefinitionGroup) => Left(fieldDefinitionGroup.fieldGroup)
      case Right(fieldDefinition) => Right(fieldDefinition.field)
    }
    FieldGroup(groupName, convertedChildren)
  }

  def fieldDefinitions:List[FieldDefinition] = {
    children.flatMap {
      case Left(childFieldDefinitionGroup) => childFieldDefinitionGroup.fieldDefinitions
      case Right(childFieldDefinition) => List(childFieldDefinition)
    }
  }

  def fieldDefinition(field:Field) = fieldDefinitions.find(_.field == field).get
}
object FieldDefinitionGroup {
  val Empty = FieldDefinitionGroup("Fields", Nil)
  def apply(fields:Field*) = {
    new FieldDefinitionGroup("Fields", fields.map(field => Right(DefaultFieldDefinition(field))).toList)
  }
}
