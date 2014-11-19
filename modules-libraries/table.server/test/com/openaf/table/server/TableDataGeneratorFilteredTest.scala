package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.lib.api.{FilterFieldKey, RowHeaderFieldKey, TableState}
import com.openaf.table.lib.api.TableValues._
import com.openaf.table.server.datasources.DataSourceTestData._

class TableDataGeneratorFilteredTest extends FunSuite {
  test("1 row (filtered), 0 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))

    val expectedRows = List(row(0, List(FieldInt)), row(1, List(1)))
    val expectedValueLookUp = Map(genderField.id -> List(genderField.id, F, M))
    val expectedFieldValues = orderedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (1st filtered), 0 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField, NameField))

    val expectedRows = List(row(0, List(FieldInt, FieldInt)), row(1, List(1,3)), row(2, List(1,2)), row(3, List(1,1)))
    val expectedValueLookUp = Map(
      genderField.id -> List(genderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie)
    )
    val expectedFieldValues = orderedGenderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      Map(NameField.withKey(RowHeaderFieldKey(1)) -> List(3,2,1))

    check(tableState, expectedRows, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 1 filter (not filtered)") {
    val tableState = TableState.Blank.withFilterFields(List(NameField))

    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField.withKey(FilterFieldKey(0)))

    check(tableState, Nil, expectedFieldValues, expectedValueLookUp)
  }
}
