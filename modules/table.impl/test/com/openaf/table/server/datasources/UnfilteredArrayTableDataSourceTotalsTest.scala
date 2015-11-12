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

  test("1 row (bottom grand total), 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))
      .toggleBottomRowGrandTotal

    val expectedRowHeaderValues = Set(List(1), List(2), List(TotalBottomInt))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1             )(ch) -> 180,
      p(2             )(ch) -> 245,
      p(TotalBottomInt)(ch) -> 425
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row (bottom grand total), 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))
      .toggleBottomRowGrandTotal

    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(p(1)(ch) -> 425)
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))
    val expectedFieldValues = scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (bottom grand total), 1 measure, 1 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GroupField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(GenderField)))
      .toggleBottomRowGrandTotal

    val expectedRowHeaderValues = Set(List(1), List(TotalBottomInt))
    val chs = Array(
      List(0,1,0),
      List(0,2,0)
    )
    val expectedColHeaders = chs.toSet
    val expectedData = Map(
      p(1             )(chs(0)) -> 180,
      p(1             )(chs(1)) -> 245,
      p(TotalBottomInt)(chs(0)) -> 180,
      p(TotalBottomInt)(chs(1)) -> 245
    )
    val expectedValueLookUp = Map(
      GroupField.id -> List(GroupField.id, Friends),
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = groupFieldValues(GroupField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0))) ++
      genderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (bottom grand total), 1 measure (right total), 1 column") {
    val scoreField = ScoreField.withTotals(Totals(bottom = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GroupField))
      .withColumnHeaderLayout(ColumnHeaderLayout(scoreField, List(GenderField)))
      .toggleBottomRowGrandTotal

    val expectedRowHeaderValues = Set(List(1), List(TotalBottomInt))
    val chs = Array(
      List(0,1             ,0),
      List(0,2             ,0),
      List(0,TotalBottomInt,0)
    )
    val expectedColHeaders = chs.toSet
    val expectedData = Map(
      p(1             )(chs(0)) -> 180,
      p(1             )(chs(1)) -> 245,
      p(TotalBottomInt)(chs(0)) -> 180,
      p(TotalBottomInt)(chs(1)) -> 245,
      p(TotalBottomInt)(chs(2)) -> 425
    )
    val expectedValueLookUp = Map(
      GroupField.id -> List(GroupField.id, Friends),
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = groupFieldValues(GroupField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField.withKey(ColumnHeaderFieldKey(0))) ++
      genderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (top grand total), 1 measure, 1 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GroupField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(GenderField)))
      .toggleTopRowGrandTotal

    val expectedRowHeaderValues = Set(List(TotalTopInt), List(1))
    val chs = Array(
      List(0,1,0),
      List(0,2,0)
    )
    val expectedColHeaders = chs.toSet
    val expectedData = Map(
      p(1          )(chs(0)) -> 180,
      p(1          )(chs(1)) -> 245,
      p(TotalTopInt)(chs(0)) -> 180,
      p(TotalTopInt)(chs(1)) -> 245
    )
    val expectedValueLookUp = Map(
      GroupField.id -> List(GroupField.id, Friends),
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = groupFieldValues(GroupField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0))) ++
      genderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (top grand total), 1 measure (left total), 1 column") {
    val scoreField = ScoreField.withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GroupField))
      .withColumnHeaderLayout(ColumnHeaderLayout(scoreField, List(GenderField)))
      .toggleTopRowGrandTotal

    val expectedRowHeaderValues = Set(List(TotalTopInt), List(1))
    val chs = Array(
      List(0,TotalTopInt,0),
      List(0,1          ,0),
      List(0,2          ,0)
    )
    val expectedColHeaders = chs.toSet
    val expectedData = Map(
      p(TotalTopInt)(chs(0)) -> 425,
      p(TotalTopInt)(chs(1)) -> 180,
      p(TotalTopInt)(chs(2)) -> 245,
      p(1          )(chs(1)) -> 180,
      p(1          )(chs(2)) -> 245

    )
    val expectedValueLookUp = Map(
      GroupField.id -> List(GroupField.id, Friends),
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = groupFieldValues(GroupField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField.withKey(ColumnHeaderFieldKey(0))) ++
      genderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }
}
