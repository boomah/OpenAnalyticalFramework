package com.openaf.table.gui

import com.openaf.table.lib.api._

object GUITestData {
  val NameField = Field[String]("name")
  val GenderField = Field[String]("gender")
  val LocationField = Field[String]("location")
  val AgeField = Field[Int]("age")
  val ScoreField = Field[Int]("score", Measure)

  val Fields = List(NameField, GenderField, LocationField, AgeField, ScoreField)

  val FieldGroupData = FieldGroup("Fields", Fields.map(Right(_)))

  val GenderValuesLookUp:Map[FieldID,Array[Any]] = Map(GenderField.id -> Array("Gender", "F", "M"))

  val DefaultRenderers:Map[Field[_],Renderer[_]] = Map(GenderField -> StringRenderer)
}
