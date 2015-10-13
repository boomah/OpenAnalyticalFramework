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
  val GroupField = Field[String]("group")
  val AgeField = Field[Int]("age")
  val ScoreField = Field[Int]("score", Measure)

  private val StringFields = List(NameField, GenderField, LocationField, GroupField)
  private val StringFieldDefinitions = StringFields.map(field => StringFieldDefinition(field))
  private val IntFields = List(AgeField, ScoreField)
  private val IntFieldDefinitions = IntFields.map(field => IntFieldDefinition(field))
  private val FieldDefinitions:List[FieldDefinition] = StringFieldDefinitions ::: IntFieldDefinitions

  val FieldIDs = FieldDefinitions.map(_.defaultField.id).toArray
  val Group = FieldDefinitionGroup("Fields", FieldDefinitions.map(definition => Right(definition)))
  val Groups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard, Group))

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

  val Friends = "Friends"

  val data:Array[Array[Any]] = Array(
    Array(Rosie, F, London,     Friends, 36, 50),
    Array(Laura, F, Manchester, Friends, 36, 60),
    Array(Josie, F, Manchester, Friends, 31, 70),
    Array(Nick,  M, London,     Friends, 34, 80),
    Array(Paul,  M, Manchester, Friends, 32, 90),
    Array(Ally,  M, Edinburgh,  Friends, 34, 75)
  )

  val EmptyListSet:Set[List[Int]] = Set(Nil)
  val EmptySet:Set[List[Int]] = Set.empty
  val EmptyMapList:List[Map[(List[Int],List[Int]),Int]] = List(Map.empty)

  val dataSource = new TestTableDataSource(Groups, FieldIDs, data)

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
  def orderedGroupFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1))
  def countFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)

  def row(rowIndex:Int=0, rowHeaderValues:List[Int]=Nil, columnHeaderAndDataValues:List[Any]=Nil) = {
    new OpenAFTableRow(rowIndex, rowHeaderValues.toArray, columnHeaderAndDataValues.toArray)
  }

  def mInt(value:Int) = new MutInt(value)

  def p(rowHeaderValues:Int*)(columnHeaders:List[Int]) = (rowHeaderValues.toList, columnHeaders)

  def check(tableState:TableState, expectedRowHeaderValues:Set[List[Int]],
            expectedColumnHeaderValues:Set[List[Int]], expectedData:Map[(List[Int],List[Int]),Any],
            expectedFieldValues:Map[Field[_],List[Int]], expectedValueLookUp:Map[FieldID,List[Any]]) {
    val pivotData = dataSource.pivotData(tableState.generateFieldKeys)
    assert(pivotData.rowHeaderValues.map(_.toList).toSet === expectedRowHeaderValues)
    assert(pivotData.columnHeaderValues.map(_.toList).toSet === expectedColumnHeaderValues)
    val aggregatorData = expectedData.map{case ((row,column),_) => (row,column) -> pivotData.aggregator(row.toArray,column.toArray)}
    assert(aggregatorData === expectedData)
    assert(pivotData.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
    assert(pivotData.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
  }

  def check(tableState:TableState,
            expectedRows:List[OpenAFTableRow],
            expectedFieldValues:Map[Field[_],List[Int]],
            expectedValueLookUp:Map[FieldID,List[Any]]) {
    val tableData = dataSource.tableData(tableState.generateFieldKeys)
    val rows = tableData.tableValues.rows.toList
    assert(rows.map(_.row) === expectedRows.map(_.row))
    assert(rows.map(_.rowHeaderValues.toList) === expectedRows.map(_.rowHeaderValues.toList))
    assert(rows.map(_.columnHeaderAndDataValues.toList) === expectedRows.map(_.columnHeaderAndDataValues.toList))
    assert(tableData.tableValues.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
    assert(tableData.tableValues.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
  }
}

class TestTableDataSource(fieldDefinitionGroups:FieldDefinitionGroups, fieldIDs:Array[FieldID],
                          data:Array[Array[Any]]) extends UnfilteredArrayTableDataSource {
  override def dataSourceTable(tableState:TableState) = DataSourceTable(fieldIDs, data, fieldDefinitionGroups)
}
