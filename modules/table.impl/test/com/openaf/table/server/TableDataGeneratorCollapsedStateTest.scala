package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._
import com.openaf.table.lib.api._
import TableValues._

class TableDataGeneratorCollapsedStateTest extends FunSuite {
  test("2 row (first row, first value collapsed), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllExpanded(Set(CollapsedStatePath(Array(F))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt),    List(FieldInt)),
      row(1, List(1,        TotalTopInt), List(180)),
      row(2, List(2,        3),           List(75)),
      row(3, List(2,        1),           List(80)),
      row(4, List(2,        2),           List(90))
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

  test("2 row (first row, second value collapsed), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllExpanded(Set(CollapsedStatePath(Array(M))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt),    List(FieldInt)),
      row(1, List(2,        1),           List(50)),
      row(2, List(2,        2),           List(130)),
      row(3, List(1,        TotalTopInt), List(245))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, M, F),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = Map(genderField.withKey(RowHeaderFieldKey(0)) -> List(2,1)) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column (first column, first value collapsed)") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllExpanded(Set(CollapsedStatePath(Array(GenderField.id, F))))))
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout.verticallyStacked(List(ScoreField, genderField, LocationField)))

    val expectedRows = List(
      row(0, Nil, List(FieldInt,    FieldInt, FieldInt, FieldInt)),
      row(1, Nil, List(1,           2,        2,        2       )),
      row(2, Nil, List(TotalTopInt, 3,        1,        2       )),
      row(3, Nil, List(180,         75,       80,       90      ))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedGenderFieldValues(genderField.withKey(ColumnHeaderFieldKey(1))) ++
      orderedLocationFieldValues(LocationField.withKey(ColumnHeaderFieldKey(2)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column (first column, second value collapsed)") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllExpanded(Set(CollapsedStatePath(Array(GenderField.id, M))))))
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout.verticallyStacked(List(ScoreField, genderField, LocationField)))

    val expectedRows = List(
      row(0, Nil, List(FieldInt, FieldInt, FieldInt   )),
      row(1, Nil, List(2,        2,        1          )),
      row(2, Nil, List(1,        2,        TotalTopInt)),
      row(3, Nil, List(50,       130,      245        ))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, M, F),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0))) ++
      Map(genderField.withKey(ColumnHeaderFieldKey(1)) -> List(2,1)) ++
      orderedLocationFieldValues(LocationField.withKey(ColumnHeaderFieldKey(2)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }
}
