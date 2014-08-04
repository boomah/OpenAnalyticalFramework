package com.openaf.table.server.datasources

import com.openaf.table.lib.api.{Measure, Field}
import com.openaf.table.server._
import com.openaf.table.server.StringFieldDefinition
import com.openaf.table.server.IntFieldDefinition

object DataSourceTestData {
  val NameField = Field[String]("name")
  val GenderField = Field[String]("gender")
  val LocationField = Field[String]("location")
  val AgeField = Field[Int]("age")
  val ScoreField = Field[Int]("score", Measure)

  private val StringFields = List(NameField, GenderField, LocationField)
  private val StringFieldDefinitions = StringFields.map(StringFieldDefinition)
  private val IntFields = List(AgeField, ScoreField)
  private val IntFieldDefinitions = IntFields.map(IntFieldDefinition)
  private val FieldDefinitions:List[FieldDefinition] = StringFieldDefinitions ::: IntFieldDefinitions

  val FieldIDs = FieldDefinitions.map(_.defaultField.id).toArray
  val Group = FieldDefinitionGroup("Fields", FieldDefinitions.map(definition => Right(definition)))
  val Groups = FieldDefinitionGroups(List(Group))

  val Rosie = "Rosie"
  val Laura = "Laura"
  val Josie = "Josie"
  val Nick = "Nick"
  val Paul = "Paul"
  val Ally = "Ally"

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

  def nameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3,4,5,6))
  def genderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2))
  val ScoreFieldValues:Map[Field[_],List[Int]] = Map(ScoreField -> Nil)
}
