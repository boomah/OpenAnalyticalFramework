package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.lib.api._
import com.openaf.table.server.datasources.DataSourceTestData._
import com.openaf.table.lib.api.TableValues._

class TableDataGeneratorTotalsTest extends FunSuite {
  test("2 row (first totals top), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt),    List(FieldInt)),
      row(1, List(1,        TotalTopInt), List(180)),
      row(2, List(1,        1),           List(50)),
      row(3, List(1,        2),           List(130)),
      row(4, List(2,        TotalTopInt), List(245)),
      row(5, List(2,        3),           List(75)),
      row(6, List(2,        1),           List(80)),
      row(7, List(2,        2),           List(90))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first totals bottom), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(bottom = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt),       List(FieldInt)),
      row(1, List(1,        1),              List(50)),
      row(2, List(1,        2),              List(130)),
      row(3, List(1,        TotalBottomInt), List(180)),
      row(4, List(2,        3),              List(75)),
      row(5, List(2,        1),              List(80)),
      row(6, List(2,        2),              List(90)),
      row(7, List(2,        TotalBottomInt), List(245))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }
}
