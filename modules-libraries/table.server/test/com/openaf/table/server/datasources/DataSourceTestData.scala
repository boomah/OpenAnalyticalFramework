package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import com.openaf.table.server._
import org.scalatest.Assertions._
import com.openaf.table.server.IntFieldDefinition
import com.openaf.table.server.FieldDefinitionGroups
import com.openaf.table.server.StringFieldDefinition

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

  val EmptyListSet:Set[List[Int]] = Set(Nil)
  val EmptySet:Set[List[Int]] = Set.empty
  val EmptyMapList:List[Map[(List[Int],List[Int]),Int]] = List(Map.empty)

  val dataSource = RawRowBasedTableDataSource(data, FieldIDs, Groups)

  def nameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3,4,5,6))
  def orderedNameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(6,3,2,4,5,1))
  def reversedNameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = orderedNameFieldValues(field).mapValues(_.reverse)
  def genderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2))
  def orderedGenderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2))
  def reversedGenderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = orderedGenderFieldValues(field).mapValues(_.reverse)
  def locationFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3))
  def orderedLocationFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(3,1,2))
  def reversedLocationFieldValues(field:Field[_]):Map[Field[_],List[Int]] = orderedLocationFieldValues(field).mapValues(_.reverse)
  def scoreFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)
  def measureFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)

  def row(rowIndex:Int=0, rowHeaderValues:List[Int]=Nil, columnHeaderAndDataValues:List[Any]=Nil) = {
    new OpenAFTableRow(rowIndex, rowHeaderValues.toArray, columnHeaderAndDataValues.toArray)
  }

  def check(tableState:TableState, expectedRowHeaderValues:Set[List[Int]],
            expectedColHeaderValues:List[Set[List[Int]]], expectedData:List[Map[(List[Int],List[Int]),Int]],
            expectedFieldValues:Map[Field[_],List[Int]], expectedValueLookUp:Map[FieldID,List[Any]]) {
    val result = dataSource.result(tableState.generateFieldKeys)
    assert(result.rowHeaderValues.map(_.toList).toSet === expectedRowHeaderValues)
    assert(result.pathData.map(_.colHeaderValues.map(_.toList).toSet).toList === expectedColHeaderValues)
    val convertedData = result.pathData.map(_.data.map{case (key,value) => (key.array1.toList, key.array2.toList) -> value}).toList
    assert(convertedData === expectedData)
    assert(result.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
    assert(result.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
  }

  def check(tableState:TableState,
            expectedRows:List[OpenAFTableRow],
            expectedFieldValues:Map[Field[_],List[Int]],
            expectedValueLookUp:Map[FieldID,List[Any]]) {
    val tableData = TableDataGenerator.tableData(tableState.generateFieldKeys, dataSource)
    val rows = tableData.tableValues.rows.toList
    assert(rows.map(_.row).toList === expectedRows.map(_.row))
    assert(rows.map(_.rowHeaderValues.toList) === expectedRows.map(_.rowHeaderValues.toList))
    assert(rows.map(_.columnHeaderAndDataValues.toList) === expectedRows.map(_.columnHeaderAndDataValues.toList))
    assert(tableData.tableValues.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
    assert(tableData.tableValues.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
  }
}
