package com.openaf.table.lib.api

case class FieldGroup(groupName:String, children:List[Either[FieldGroup,Field[_]]]) {
  def fields:List[Field[_]] = {
    children.flatMap {
      case Left(fieldGroup) => fieldGroup.fields
      case Right(field) => Some(field)
    }
  }
}
object FieldGroup {
  val Empty = FieldGroup("Fields", Nil)
}
