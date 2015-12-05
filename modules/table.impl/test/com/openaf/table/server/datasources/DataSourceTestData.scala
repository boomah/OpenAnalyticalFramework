package com.openaf.table.server.datasources

import java.time.LocalDate

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
  val DateField = Field[LocalDate]("date")

  private val StringFields = List(NameField, GenderField, LocationField, GroupField)
  private val StringFieldDefinitions = StringFields.map(field => StringFieldDefinition(field))
  private val DateFields = List(DateField)
  private val DateFieldDefinitions = DateFields.map(field => LocalDateFieldDefinition(field))
  private val IntFields = List(AgeField, ScoreField)
  private val IntFieldDefinitions = IntFields.map(field => IntFieldDefinition(field))
  private val FieldDefinitions:List[FieldDefinition] = StringFieldDefinitions ::: DateFieldDefinitions ::: IntFieldDefinitions

  val FieldIDs = FieldDefinitions.map(_.defaultField.id).toArray
  val Group = FieldDefinitionGroup("Fields", FieldDefinitions.map(definition => Right(definition)))
  val Groups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard, Group))

  val Rosie = "Rosie"
  val Laura = "Laura"
  val Josie = "Josie"
  val Nick = "Nick"
  val Paul = "Paul"
  val Ally = "Ally"
  val Unknown = "Unknown"

  val F = "F"
  val M = "M"

  val London = "London"
  val Manchester = "Manchester"
  val Edinburgh = "Edinburgh"

  val Friends = "Friends"

  val Date1 = LocalDate.of(2015, 5, 15)
  val Date2 = LocalDate.of(2015, 5, 14)
  val Date3 = LocalDate.of(2015, 6, 15)
  val Date4 = LocalDate.of(2014, 3, 8)
  val Date5 = LocalDate.of(2013, 5, 15)

  val MonthYear1 = LocalDateToYearMonthTransformer.transform(Date1)
  val MonthYear2 = LocalDateToYearMonthTransformer.transform(Date3)
  val MonthYear3 = LocalDateToYearMonthTransformer.transform(Date4)
  val MonthYear4 = LocalDateToYearMonthTransformer.transform(Date5)

  val data:Array[Array[Any]] = Array(
    Array(Rosie, F, London,     Friends, Date1, 36, 50),
    Array(Laura, F, Manchester, Friends, Date2, 36, 60),
    Array(Josie, F, Manchester, Friends, Date3, 31, 70),
    Array(Nick,  M, London,     Friends, Date4, 34, 80),
    Array(Paul,  M, Manchester, Friends, Date5, 32, 90),
    Array(Ally,  M, Edinburgh,  Friends, Date1, 34, 75)
  )

  val dataWithNoValues:Array[Array[Any]] = Array(
    Array(Rosie,   F,       London,     Friends, Date1,   36,      50),
    Array(Laura,   F,       Manchester, Friends, Date2,   36,      60),
    Array(Unknown, NoValue, NoValue,    NoValue, NoValue, NoValue, 70),
    Array(Nick,    M,       London,     Friends, Date4,   34,      80),
    Array(Paul,    M,       Manchester, Friends, Date5,   32,      90),
    Array(Unknown, NoValue, NoValue,    NoValue, NoValue, NoValue, 75)
  )

  val EmptyListSet:Set[List[Int]] = Set(Nil)
  val EmptySet:Set[List[Int]] = Set.empty
  val EmptyMapList:List[Map[(List[Int],List[Int]),Int]] = List(Map.empty)

  val dataSource = new TestTableDataSource(Groups, FieldIDs, data)
  val dataSourceNoValue = new TestTableDataSource(Groups, FieldIDs, dataWithNoValues)

  def nameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3,4,5,6))
  def orderedNameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(6,3,2,4,5,1))
  def reversedNameFieldValues(field:Field[_]):Map[Field[_],List[Int]] = orderedNameFieldValues(field).mapValues(_.reverse)
  def orderedNameFieldValuesNoValue(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(2,4,5,1,3))
  def genderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2))
  def orderedGenderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2))
  def reversedGenderFieldValues(field:Field[_]):Map[Field[_],List[Int]] = orderedGenderFieldValues(field).mapValues(_.reverse)
  def orderedGenderFieldValuesNoValue(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,3,2))
  def reversedGenderFieldValuesNoValue(field:Field[_]):Map[Field[_],List[Int]] = orderedGenderFieldValuesNoValue(field).mapValues(_.reverse)
  def locationFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3))
  def orderedLocationFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(3,1,2))
  def reversedLocationFieldValues(field:Field[_]):Map[Field[_],List[Int]] = orderedLocationFieldValues(field).mapValues(_.reverse)
  def scoreFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)
  def measureFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)
  def groupFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1))
  def orderedGroupFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1))
  def countFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)
  def ageFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3,4))
  def dateFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3,4,5))
  def monthYearFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> List(1,2,3,4))

  def emptyFieldValues(field:Field[_]):Map[Field[_],List[Int]] = Map(field -> Nil)

  def row(rowIndex:Int=0, rowHeaderValues:List[Int]=Nil, columnHeaderAndDataValues:List[Any]=Nil) = {
    new OpenAFTableRow(rowIndex, rowHeaderValues.toArray, columnHeaderAndDataValues.toArray)
  }

  def p(rowHeaderValues:Int*)(columnHeaders:List[Int]) = (rowHeaderValues.toList, columnHeaders)

  def check(tableState:TableState, expectedRowHeaderValues:Set[List[Int]],
            expectedColumnHeaderValues:Set[List[Int]], expectedData:Map[(List[Int],List[Int]),Any],
            expectedFieldValues:Map[Field[_],List[Int]], expectedValueLookUp:Map[FieldID,List[Any]]) = {
    val pivotData = dataSource.pivotData(tableState.generateFieldKeys)
    assert(pivotData.rowHeaderValues.map(_.toList).toSet === expectedRowHeaderValues)
    assert(pivotData.columnHeaderValues.map(_.toList).toSet === expectedColumnHeaderValues)
    if (expectedData.isEmpty && tableState.allFields.exists(_.fieldType.isMeasure)) {
      // If the expected data is empty the transform below will always work so make sure we expect the data to be empty
      assert(expectedRowHeaderValues.isEmpty && expectedColumnHeaderValues.isEmpty, "If expectedData is empty then " +
        "expectedRowHeaderValues and expectedColumnHeaderValues should be empty too")
    }
    val aggregatorData = expectedData.map{case ((row,column),_) => (row,column) -> pivotData.aggregator(row.toArray,column.toArray)}
    assert(aggregatorData === expectedData)
    assert(pivotData.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
    assert(pivotData.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
    pivotData
  }

  def checkWithDataSource(dataSource:TableDataSource, tableState:TableState,
                          expectedRows:List[OpenAFTableRow],
                          expectedFieldValues:Map[Field[_],List[Int]],
                          expectedValueLookUp:Map[FieldID,List[Any]]):Unit = {
    val tableData = dataSource.tableData(tableState.generateFieldKeys)
    val rows = tableData.tableValues.rows.toList
    assert(rows.map(_.row) === expectedRows.map(_.row))
    assert(rows.map(_.rowHeaderValues.toList) === expectedRows.map(_.rowHeaderValues.toList))
    assert(rows.map(_.columnHeaderAndDataValues.toList) === expectedRows.map(_.columnHeaderAndDataValues.toList))
    assert(tableData.tableValues.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
    assert(tableData.tableValues.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
  }

  def check(tableState:TableState,
            expectedRows:List[OpenAFTableRow],
            expectedFieldValues:Map[Field[_],List[Int]],
            expectedValueLookUp:Map[FieldID,List[Any]]) {
    checkWithDataSource(dataSource, tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  def checkNoValue(tableState:TableState,
                   expectedRows:List[OpenAFTableRow],
                   expectedFieldValues:Map[Field[_],List[Int]],
                   expectedValueLookUp:Map[FieldID,List[Any]]):Unit = {
    checkWithDataSource(dataSourceNoValue, tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }
}

class TestTableDataSource(fieldDefinitionGroups:FieldDefinitionGroups, fieldIDs:Array[FieldID],
                          data:Array[Array[Any]]) extends UnfilteredArrayTableDataSource {
  override def dataSourceTable(tableState:TableState) = DataSourceTable(fieldIDs, data, fieldDefinitionGroups)
}
