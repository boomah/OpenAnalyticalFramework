package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.Field

trait FieldDefinition {
  type T
  def defaultField:Field
  def fieldID = defaultField.id
  def primaryKey:Boolean
  def renderer:Renderer
  def ordering:Ordering[T]
}

case class AnyFieldDefinition(defaultField:Field) extends FieldDefinition {
  type T = Any
  val primaryKey = false
  val renderer = AnyRenderer
  val ordering = AnyOrdering
}

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
  def apply(fields:Field*) = {
    new FieldDefinitionGroup("Fields", fields.map(field => Right(AnyFieldDefinition(field))).toList)
  }
}
