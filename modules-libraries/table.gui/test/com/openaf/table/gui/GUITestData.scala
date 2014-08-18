package com.openaf.table.gui

import com.openaf.table.lib.api._

object GUITestData {
  val NameField = Field[String]("name")
  val GenderField = Field[String]("gender")
  val GenderFieldDuplicate = GenderField.duplicate
  val LocationField = Field[String]("location")
  val AgeField = Field[Int]("age")
  val ScoreField = Field[Int]("score", Measure)
  val ScoreFieldDuplicate = ScoreField.duplicate
  val GroupField = Field[String]("group")
  val GroupFieldDuplicate = GroupField.duplicate

  val Fields = List(NameField, GenderField, LocationField, AgeField, ScoreField, GroupField)

  val FieldGroupData = FieldGroup("Fields", Fields.map(Right(_)))

  val Rosie = "Rosie"
  val Laura = "Laura"
  val Josie = "Josie"
  val Nick = "Nick"
  val Paul = "Paul"
  val Ally = "Ally"

  val F = "F"
  val M = "M"

  val Edinburgh = "Edinburgh"
  val London = "London"
  val Manchester = "Manchester"

  val Friends = "Friends"

  val Gender = "Gender"
  val Location = "Location"
  val Age = "Age"
  val Score = "Score"
  val Group = "Group"

  val NameValuesLookUp:Map[FieldID,Array[Any]] = Map(
    NameField.id -> Array(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally)
  )
  val GenderValuesLookUp:Map[FieldID,Array[Any]] = Map(GenderField.id -> Array(Gender, F, M))
  val LocationValuesLookUp:Map[FieldID,Array[Any]] = Map(
    LocationField.id -> Array(Location, Edinburgh, London, Manchester)
  )
  val AgeValuesLookUp:Map[FieldID,Array[Any]] = Map(AgeField.id -> Array(Age))
  val ScoreValuesLookUp:Map[FieldID,Array[Any]] = Map(ScoreField.id -> Array(Score))
  val GroupValuesLookUp:Map[FieldID,Array[Any]] = Map(GroupField.id -> Array(Group, Friends))

  def nameFieldValues(field:Field[_]):Map[Field[_],Array[Int]] = Map(field -> Array(1,2,3,4,5,6))

  val DefaultRenderers:Map[Field[_],Renderer[_]] = Map(
    NameField -> StringRenderer,
    GenderField -> StringRenderer,
    GenderFieldDuplicate -> StringRenderer,
    LocationField -> StringRenderer,
    AgeField -> IntRenderer,
    ScoreField -> IntRenderer,
    ScoreFieldDuplicate -> IntRenderer,
    GroupField -> StringRenderer,
    GroupFieldDuplicate -> StringRenderer
  )
}
