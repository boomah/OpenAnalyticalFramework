package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._
import com.openaf.table.lib.api._
import com.openaf.table.lib.api.TableValues._
import StandardFields._

class TableDataGeneratorTest extends FunSuite {
  test("1 row (key), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField))

    val expectedRows = List(
      row(0, List(FieldInt)),
      row(1, List(6)),
      row(2, List(3)),
      row(3, List(2)),
      row(4, List(4)),
      row(5, List(5)),
      row(6, List(1))
    )
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key, reversed), 0 measure, 0 column") {
    val nameField = NameField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(nameField))

    val expectedRows = List(
      row(0, List(FieldInt)),
      row(1, List(1)),
      row(2, List(5)),
      row(3, List(4)),
      row(4, List(2)),
      row(5, List(3)),
      row(6, List(6))
    )
    val expectedValueLookUp = Map(nameField.id -> List(nameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = reversedNameFieldValues(nameField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))

    val expectedRows = List(
      row(0, List(FieldInt)),
      row(1, List(1)),
      row(2, List(2))
    )
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (reversed), 0 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))

    val expectedRows = List(
      row(0, List(FieldInt)),
      row(1, List(2)),
      row(2, List(1))
    )
    val expectedValueLookUp = Map(genderField.id -> List(genderField.id, F, M))
    val expectedFieldValues = reversedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt), List(FieldInt)),
      row(1, List(6), List(75)),
      row(2, List(3), List(70)),
      row(3, List(2), List(60)),
      row(4, List(4), List(80)),
      row(5, List(5), List(90)),
      row(6, List(1), List(50))
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, LocationField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt)),
      row(1, List(1,1)),
      row(2, List(1,2)),
      row(3, List(2,3)),
      row(4, List(2,1)),
      row(5, List(2,2))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (row 1 reversed), 0 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField, LocationField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt)),
      row(1, List(2,3)),
      row(2, List(2,1)),
      row(3, List(2,2)),
      row(4, List(1,1)),
      row(5, List(1,2))
    )
    val expectedValueLookUp = Map(
      genderField.id -> List(genderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = reversedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (row 2 reversed), 0 measure, 0 column") {
    val locationField = LocationField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, locationField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt)),
      row(1, List(1,2)),
      row(2, List(1,1)),
      row(3, List(2,2)),
      row(4, List(2,1)),
      row(5, List(2,3))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      locationField.id -> List(locationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      reversedLocationFieldValues(locationField.withKey(RowHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt), List(FieldInt)),
      row(1, List(1,1), List(50)),
      row(2, List(1,2), List(130)),
      row(3, List(2,3), List(75)),
      row(4, List(2,1), List(80)),
      row(5, List(2,2), List(90))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (row 1 reversed), 1 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt), List(FieldInt)),
      row(1, List(2,3), List(75)),
      row(2, List(2,1), List(80)),
      row(3, List(2,2), List(90)),
      row(4, List(1,1), List(50)),
      row(5, List(1,2), List(130))
    )
    val expectedValueLookUp = Map(
      genderField.id -> List(genderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = reversedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (row 2 reversed), 1 measure, 0 column") {
    val locationField = LocationField.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, locationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt), List(FieldInt)),
      row(1, List(1,2), List(130)),
      row(2, List(1,1), List(50)),
      row(3, List(2,2), List(90)),
      row(4, List(2,1), List(80)),
      row(5, List(2,3), List(75))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      reversedLocationFieldValues(locationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedRows = List(row(0, Nil, List(6,3,2,4,5,1)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (key, reversed)") {
    val nameField = NameField.flipSortOrder
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(nameField))

    val expectedRows = List(row(0, Nil, List(1,5,4,2,3,6)))
    val expectedValueLookUp = Map(nameField.id -> List(nameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = reversedNameFieldValues(nameField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val expectedRows = List(
      row(0, Nil, List.fill(6)(FieldInt)),
      row(1, Nil, List(6, 3, 2, 4, 5, 1)),
      row(2, Nil, List(75,70,60,80,90,50))
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(ColumnHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (right of measure)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(ScoreField, GenderField), Nil))

    val expectedRows = List(
      row(0, Nil, List(FieldInt, 1,       2)),
      row(1, Nil, List(425,      NoValue, NoValue))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (left of measure)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(GenderField, ScoreField), Nil))

    val expectedRows = List(
      row(0, Nil, List(1,       2,       FieldInt)),
      row(1, Nil, List(NoValue, NoValue, 425))
    )
    val expectedValueLookUp = Map(
      ScoreField.id -> List(ScoreField.id),
      GenderField.id -> List(GenderField.id, F, M)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 0 measure, 1 column (key, same as row)") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedRows = List(
      row(0, List(FieldInt), List(6,3,2,4,5,1)),
      row(1, List(6), List.fill(6)(NoValue)),
      row(2, List(3), List.fill(6)(NoValue)),
      row(3, List(2), List.fill(6)(NoValue)),
      row(4, List(4), List.fill(6)(NoValue)),
      row(5, List(5), List.fill(6)(NoValue)),
      row(6, List(1), List.fill(6)(NoValue))
    )
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key, reversed), 0 measure, 1 column (key, same as row)") {
    val nameField1 = NameField.flipSortOrder
    val nameField2 = NameField
    val tableState = TableState.Blank
      .withRowHeaderFields(List(nameField1))
      .withColumnHeaderLayout(ColumnHeaderLayout(nameField2))

    val expectedRows = List(
      row(0, List(FieldInt), List(6,3,2,4,5,1)),
      row(1, List(1), List.fill(6)(NoValue)),
      row(2, List(5), List.fill(6)(NoValue)),
      row(3, List(4), List.fill(6)(NoValue)),
      row(4, List(2), List.fill(6)(NoValue)),
      row(5, List(3), List.fill(6)(NoValue)),
      row(6, List(6), List.fill(6)(NoValue))
    )
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = reversedNameFieldValues(nameField1.withKey(RowHeaderFieldKey(0))) ++
      orderedNameFieldValues(nameField2.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 0 measure, 1 column (key, same as row but reversed)") {
    val nameField = NameField.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(nameField))

    val expectedRows = List(
      row(0, List(FieldInt), List(1,5,4,2,3,6)),
      row(1, List(6), List.fill(6)(NoValue)),
      row(2, List(3), List.fill(6)(NoValue)),
      row(3, List(2), List.fill(6)(NoValue)),
      row(4, List(4), List.fill(6)(NoValue)),
      row(5, List(5), List.fill(6)(NoValue)),
      row(6, List(1), List.fill(6)(NoValue))
    )
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      reversedNameFieldValues(nameField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 1 measure, 1 column (key, same as row but reversed)") {
    val nameField = NameField.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(nameField)))

    val expectedRows = List(
      row(0, List(NoValueInt), List.fill(6)(FieldInt)),
      row(1, List(FieldInt), List(1,5,4,2,3,6)),
      row(2, List(6), List(NoValue,NoValue,NoValue,NoValue,NoValue,75)),
      row(3, List(3), List(NoValue,NoValue,NoValue,NoValue,70,NoValue)),
      row(4, List(2), List(NoValue,NoValue,NoValue,60,NoValue,NoValue)),
      row(5, List(4), List(NoValue,NoValue,80,NoValue,NoValue,NoValue)),
      row(6, List(5), List(NoValue,90,NoValue,NoValue,NoValue,NoValue)),
      row(7, List(1), List(50,NoValue,NoValue,NoValue,NoValue,NoValue))
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      reversedNameFieldValues(nameField.withKey(ColumnHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column (one under the other)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(GenderField, List(LocationField)))

    val expectedRows = List(
      row(0, Nil, List(1,1,2,2,2)),
      row(1, Nil, List(1,2,3,1,2))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedLocationFieldValues(LocationField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column (one under the other and reversed)") {
    val locationField = LocationField.flipSortOrder
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(GenderField, List(locationField)))

    val expectedRows = List(
      row(0, Nil, List(1,1,2,2,2)),
      row(1, Nil, List(2,1,2,1,3))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(0))) ++
      reversedLocationFieldValues(locationField.withKey(ColumnHeaderFieldKey(1)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 3 column (one with multiple values on top of the other two)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(GenderField, List(NameField, LocationField)))

    val expectedRows = List(
      row(0, Nil, List(1,1,1,1,1,2,2,2,2,2,2)),
      row(1, Nil, List(3,2,1,1,2,6,4,5,3,1,2))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedNameFieldValues(NameField.withKey(ColumnHeaderFieldKey(1))) ++
      orderedLocationFieldValues(LocationField.withKey(ColumnHeaderFieldKey(2)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 4 column (one field repeated with different fields underneath)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(
      List(
        ColumnHeaderTree(List(GenderField), List(NameField)),
        ColumnHeaderTree(List(GenderField), List(LocationField))
      )
    ))

    val expectedRows = List(
      row(0, Nil, List(1,1,1,2,2,2,1,1,2,2,2)),
      row(1, Nil, List(3,2,1,6,4,5,1,2,3,1,2))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(0))) ++
      orderedNameFieldValues(NameField.withKey(ColumnHeaderFieldKey(1))) ++
      orderedGenderFieldValues(GenderField.withKey(ColumnHeaderFieldKey(2))) ++
      orderedLocationFieldValues(LocationField.withKey(ColumnHeaderFieldKey(3)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (name), 1 measure (count), 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(CountField))

    val expectedRows = List(
      row(0, List(FieldInt), List(FieldInt)),
      row(1, List(6),        List(1)       ),
      row(2, List(3),        List(1)       ),
      row(3, List(2),        List(1)       ),
      row(4, List(4),        List(1)       ),
      row(5, List(5),        List(1)       ),
      row(6, List(1),        List(1)       )
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      CountField.id -> List(CountField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      countFieldValues(CountField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (gender), 1 measure (count), 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(CountField))

    val expectedRows = List(
      row(0, List(FieldInt), List(FieldInt)),
      row(1, List(1),        List(3)       ),
      row(2, List(2),        List(3)       )
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      CountField.id -> List(CountField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      countFieldValues(CountField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }
}
