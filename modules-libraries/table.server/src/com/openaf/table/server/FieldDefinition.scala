package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.Field
import java.util.{Set => JSet}

trait FieldDefinition {
  type V
  type C
  def defaultField:Field[V]
  def fieldID = defaultField.id
  def primaryKey:Boolean
  def renderer:Renderer[V]
  def ordering:Ordering[V]
  def combiner:Combiner[C,V]
  def combine(combinedValue:C, value:V) = combiner.combine(combinedValue, value)
}

case object NullFieldDefinition extends FieldDefinition {
  type V = Null
  type C = Null
  def defaultField = Field.Null
  def primaryKey = false
  def renderer = NullRenderer
  def ordering = NullOrdering
  def combiner = NullCombiner
}

case class AnyFieldDefinition(defaultField:Field[Any]) extends FieldDefinition {
  type V = Any
  type C = JSet[Any]
  val primaryKey = false
  val renderer = AnyRenderer
  val ordering = AnyOrdering
  val combiner = AnyCombiner
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
  def apply(fields:Field[Any]*) = {
    new FieldDefinitionGroup("Fields", fields.map(field => Right(AnyFieldDefinition(field))).toList)
  }
}
