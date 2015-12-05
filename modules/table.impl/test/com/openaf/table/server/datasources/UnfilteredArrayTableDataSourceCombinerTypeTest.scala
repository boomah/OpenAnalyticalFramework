package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.DataSourceTestData._
import org.scalatest.FunSuite

class UnfilteredArrayTableDataSourceCombinerTypeTest  extends FunSuite {
  private def testStandardLayout(combinerType:CombinerType, value1:Int, value2:Int):Unit = {
    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0)).withCombinerType(combinerType)
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(scoreField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val ch = List(0,0)
    val expectedColHeaders = Set(List(0,0))
    val expectedData = Map(
      p(1)(ch) -> value1,
      p(2)(ch) -> value2
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(scoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField)

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 1 measure (mean), 0 column") {
    testStandardLayout(Mean, 60, 81)
  }

  test("1 row, 1 measure (max), 0 column") {
    testStandardLayout(Max, 70, 90)
  }

  test("1 row, 1 measure (min), 0 column") {
    testStandardLayout(Min, 50, 75)
  }

  test("1 row, 1 measure (mean), 0 column, 1 filter") {
    val nameField = NameField.withFilter(RejectFilter(Set(Nick))).withKey(FilterFieldKey(0))
    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0)).withCombinerType(Mean)
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(scoreField)).withFilterFields(List(nameField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val ch = List(0,0)
    val expectedColHeaders = Set(List(0,0))
    val expectedData = Map(
      p(1)(ch) -> 60,
      p(2)(ch) -> 82
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(scoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField) ++ nameFieldValues(nameField)

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1, row, 1 measure (transformed to Double, mean), 0 column") {
    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0)).withCombinerType(Mean).withTransformerType(IntToDoubleTransformerType)
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(scoreField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val ch = List(0,0)
    val expectedColHeaders = Set(List(0,0))
    val expectedData = Map(
      p(1)(ch) -> 60.0,
      p(2)(ch) -> 245.0 / 3.0
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(scoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField)

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }
}
