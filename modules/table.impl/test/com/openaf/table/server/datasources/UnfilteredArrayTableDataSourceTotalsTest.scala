package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._
import TableValues._

class UnfilteredArrayTableDataSourceTotalsTest extends FunSuite {
  test("1 row (totals top), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1)(ch) -> 180,
      p(2)(ch) -> 245
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first totals top), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,TotalTopInt), List(1,1), List(1,2), List(2,TotalTopInt), List(2,1), List(2,2), List(2,3))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,TotalTopInt)(ch) -> 180,
      p(1,1          )(ch) -> 50,
      p(1,2          )(ch) -> 130,
      p(2,TotalTopInt)(ch) -> 245,
      p(2,1          )(ch) -> 80,
      p(2,2          )(ch) -> 90,
      p(2,3          )(ch) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first totals bottom), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(bottom = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,TotalBottomInt), List(1,1), List(1,2), List(2,TotalBottomInt), List(2,1), List(2,2), List(2,3))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,TotalBottomInt)(ch) -> 180,
      p(1,1             )(ch) -> 50,
      p(1,2             )(ch) -> 130,
      p(2,TotalBottomInt)(ch) -> 245,
      p(2,1             )(ch) -> 80,
      p(2,2             )(ch) -> 90,
      p(2,3             )(ch) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }
}
