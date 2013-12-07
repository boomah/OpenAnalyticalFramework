package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{Measure, Field}
import com.openaf.table.server.FieldDefinitionGroup

object DataSourceTestData {
  val NameField = Field("name")
  val GenderField = Field("gender")
  val LocationField = Field("location")
  val AgeField = Field("age")
  val ScoreField = Field("score", Measure)

  private val Fields = Array(NameField, GenderField, LocationField, AgeField, ScoreField)
  val FieldIDs = Fields.map(_.id)
  val Group = FieldDefinitionGroup(Fields:_*)

  val Rosie = "Rosie"
  val Laura = "Laura"
  val Josie = "Josie"
  val Nick = "Nick"
  val Paul = "Paul"
  val Ally = "Edinburgh"

  val F = "F"
  val M = "M"

  val London = "London"
  val Manchester = "Manchester"
  val Edinburgh = "Edinburgh"

  val data:Array[Array[Any]] = Array(
    Array(Rosie, F, London,     36, 50),
    Array(Laura, F, Manchester, 36, 60),
    Array(Josie, F, Manchester, 31, 70),
    Array(Nick,  M, London,     34, 80),
    Array(Paul,  M, Manchester, 32, 90),
    Array(Ally,  M, Edinburgh,  34, 75)
  )
}
