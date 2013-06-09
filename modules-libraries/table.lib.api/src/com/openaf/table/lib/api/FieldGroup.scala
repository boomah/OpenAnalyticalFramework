package com.openaf.table.lib.api

case class FieldGroup(groupName:String, children:List[Either[FieldGroup,Field]]) {
  def fields:List[Field] = {
    children.flatMap(fieldGroupOrField => {
      fieldGroupOrField match {
        case Left(fieldGroup) => fieldGroup.fields
        case Right(field) => Some(field)
      }
    })
  }
}
object FieldGroup {
  val Empty = FieldGroup("Fields", Nil)
}
