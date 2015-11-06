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

  test("0 row, 1 measure, 2 column (vertically stacked, first totals top)") {
    val genderField = GenderField.withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout.verticallyStacked(List(ScoreField, genderField, LocationField)))

    val expectedRows = List(
      row(0, Nil, List(FieldInt,    FieldInt, FieldInt, FieldInt,    FieldInt, FieldInt, FieldInt)),
      row(1, Nil, List(1,           1,        1,        2,           2,        2,        2       )),
      row(2, Nil, List(TotalTopInt, 1,        2,        TotalTopInt, 3,        1,        2       )),
      row(3, Nil, List(180,         50,       130,      245,         75,       80,       90      ))
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

  test("0 row, 1 measure, 2 column (vertically stacked, first totals bottom)") {
    val genderField = GenderField.withTotals(Totals(bottom = true))
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout.verticallyStacked(List(ScoreField, genderField, LocationField)))

    val expectedRows = List(
      row(0, Nil, List(FieldInt, FieldInt, FieldInt,       FieldInt, FieldInt, FieldInt, FieldInt      )),
      row(1, Nil, List(1,        1,        1,              2,        2,        2,        2             )),
      row(2, Nil, List(1,        2,        TotalBottomInt, 3,        1,        2,        TotalBottomInt)),
      row(3, Nil, List(50,       130,      180,            75,       80,       90,       245           ))
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

  test("2 row (first totals top), 1 measure, 2 column (vertically stacked, first totals top)") {
    val genderField = GenderField.withTotals(Totals(top = true))
    val groupField = GroupField.withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(groupField, NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout.verticallyStacked(List(ScoreField, genderField, LocationField)))

    val expectedRows = List(
      row(0, List(NoValueInt, NoValueInt),  List(FieldInt,    FieldInt, FieldInt, FieldInt,    FieldInt, FieldInt, FieldInt)),
      row(1, List(NoValueInt, NoValueInt),  List(1,           1,        1,        2,           2,        2,        2       )),
      row(2, List(FieldInt,   FieldInt),    List(TotalTopInt, 1,        2,        TotalTopInt, 3,        1,        2       )),
      row(3, List(1,          TotalTopInt), List(180,         50,       130,      245,         75,       80,       90      )),
      row(4, List(1,          6),           List(NoValue,     NoValue,  NoValue,  75,          75,       NoValue,  NoValue )),
      row(5, List(1,          3),           List(70,          NoValue,  70,       NoValue,     NoValue,  NoValue,  NoValue )),
      row(6, List(1,          2),           List(60,          NoValue,  60,       NoValue,     NoValue,  NoValue,  NoValue )),
      row(7, List(1,          4),           List(NoValue,     NoValue,  NoValue,  80,          NoValue,  80,       NoValue )),
      row(8, List(1,          5),           List(NoValue,     NoValue,  NoValue,  90,          NoValue,  NoValue,  90      )),
      row(9, List(1,          1),           List(50,          50,       NoValue,  NoValue,     NoValue,  NoValue,  NoValue ))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id),
      GroupField.id -> List(GroupField.id, Friends),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally)
    )
    val expectedFieldValues = orderedGroupFieldValues(groupField.withKey(RowHeaderFieldKey(0))) ++
      orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedGenderFieldValues(genderField.withKey(ColumnHeaderFieldKey(1))) ++
      orderedLocationFieldValues(LocationField.withKey(ColumnHeaderFieldKey(2)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure (totals top), 1 column") {
    val scoreField = ScoreField.withTotals(Totals(top = true))
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(scoreField, List(GenderField)))

    val expectedRows = List(
      row(0, Nil, List(FieldInt,    FieldInt, FieldInt)),
      row(1, Nil, List(TotalTopInt, 1,        2       )),
      row(2, Nil, List(425,         180,      245     ))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = scoreFieldValues(scoreField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("3 row (middle filtered and totals top), 1 measure, 0 column, ") {
    val locationField = LocationField.withFilter(new RetainFilter[String](Set(Manchester, London))).withTotals(Totals(top = true))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, locationField, NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt, FieldInt   ), List(FieldInt)),
      row(1, List(1,        1,        TotalTopInt), List(50)),
      row(2, List(1,        1,        1          ), List(50)),
      row(3, List(1,        2,        TotalTopInt), List(130)),
      row(4, List(1,        2,        3          ), List(70)),
      row(5, List(1,        2,        2          ), List(60)),
      row(6, List(2,        1,        TotalTopInt), List(80)),
      row(7, List(2,        1,        4          ), List(80)),
      row(8, List(2,        2,        TotalTopInt), List(90)),
      row(9, List(2,        2,        5          ), List(90))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(locationField.withKey(RowHeaderFieldKey(1))) ++
      Map(NameField.withKey(RowHeaderFieldKey(2)) -> List(3,2,4,5,1)) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }
}
