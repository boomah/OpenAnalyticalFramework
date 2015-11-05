package com.openaf.table.server

import com.openaf.table.lib.api._
import com.openaf.table.lib.api.TableValues._
import com.openaf.table.server.datasources.DataSourceTestData._
import org.scalatest.FunSuite

class TableDataGeneratorNoValueTest extends FunSuite {
  test("1 row (with NoValue), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))

    val expectedRows = List(
      row(0, List(FieldInt)),
      row(1, List(1       )),
      row(2, List(3       )),
      row(3, List(2       ))
    )
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, NoValue, M))
    val expectedFieldValues = orderedGenderFieldValuesNoValue(GenderField.withKey(RowHeaderFieldKey(0)))

    checkNoValue(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (with NoValue, reversed), 0 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))

    val expectedRows = List(
      row(0, List(FieldInt)),
      row(1, List(2       )),
      row(2, List(3       )),
      row(3, List(1       ))
    )
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, NoValue, M))
    val expectedFieldValues = reversedGenderFieldValuesNoValue(genderField.withKey(RowHeaderFieldKey(0)))

    checkNoValue(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField, GenderField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt)),
      row(1, List(2,        1)),
      row(2, List(4,        3)),
      row(3, List(5,        3)),
      row(4, List(1,        1)),
      row(5, List(3,        2))
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Unknown, Nick, Paul),
      GenderField.id -> List(GenderField.id, F, NoValue, M)
    )
    val expectedFieldValues = orderedNameFieldValuesNoValue(NameField.withKey(RowHeaderFieldKey(0))) ++
      orderedGenderFieldValuesNoValue(GenderField.withKey(RowHeaderFieldKey(1)))

    checkNoValue(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (NoValue first), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, NameField))

    val expectedRows = List(
      row(0, List(FieldInt, FieldInt)),
      row(1, List(1,        2)),
      row(2, List(1,        1)),
      row(3, List(3,        4)),
      row(4, List(3,        5)),
      row(5, List(2,        3))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, NoValue, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Unknown, Nick, Paul)
    )
    val expectedFieldValues = orderedGenderFieldValuesNoValue(GenderField.withKey(RowHeaderFieldKey(0))) ++
      orderedNameFieldValuesNoValue(NameField.withKey(RowHeaderFieldKey(1)))

    checkNoValue(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (with NoValue") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(GenderField))

    val expectedRows = List(row(0, Nil, List(1,3,2)))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, NoValue, M))
    val expectedFieldValues = orderedGenderFieldValuesNoValue(GenderField.withKey(ColumnHeaderFieldKey(0)))

    checkNoValue(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (with NoValue, reversed") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(genderField))

    val expectedRows = List(row(0, Nil, List(2,3,1)))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, NoValue, M))
    val expectedFieldValues = reversedGenderFieldValuesNoValue(genderField.withKey(ColumnHeaderFieldKey(0)))

    checkNoValue(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }
}
