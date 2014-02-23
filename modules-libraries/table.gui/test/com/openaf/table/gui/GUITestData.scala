package com.openaf.table.gui

import com.openaf.table.lib.api._

object GUITestData {
  val NameField = Field[String]("name")
  val GenderField = Field[String]("gender")
  val LocationField = Field[String]("location")
  val AgeField = Field[Int]("age")
  val ScoreField = Field[Int]("score", Measure)
  val GroupField = Field[String]("group")

  val Fields = List(NameField, GenderField, LocationField, AgeField, ScoreField, GroupField)

  val FieldGroupData = FieldGroup("Fields", Fields.map(Right(_)))

  val GenderValuesLookUp:Map[FieldID,Array[Any]] = Map(GenderField.id -> Array("Gender", "F", "M"))
  val LocationValuesLookUp:Map[FieldID,Array[Any]] = Map(
    LocationField.id -> Array("Gender", "Edinburgh", "London", "Manchester")
  )
  val GroupValuesLookUp:Map[FieldID,Array[Any]] = Map(GroupField.id -> Array("Group", "Friends"))

  val DefaultRenderers:Map[Field[_],Renderer[_]] = Map(
    GenderField -> StringRenderer,
    LocationField -> StringRenderer,
    GroupField -> StringRenderer
  )
}
