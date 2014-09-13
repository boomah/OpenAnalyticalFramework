package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class RawRowBasedTableDataSourceFilteredTest extends FunSuite {
  test("1 row (filtered), 0 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))

    val expectedRowHeaderValues = Set(List(1))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (1st filtered), 0 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField, NameField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(1,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) +
      (NameField.withKey(RowHeaderFieldKey(1)) -> List(1,2,3))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (1st filtered 2nd value), 0 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(M)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField, NameField))

    val expectedRowHeaderValues = Set(List(2,1), List(2,2), List(2,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Nick, Paul, Ally)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) +
      (NameField.withKey(RowHeaderFieldKey(1)) -> List(1,2,3))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (2nd filtered), 0 measure, 0 column") {
    val nameField = NameField.withSingleFilter(Josie)
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, nameField))

    val expectedRowHeaderValues = Set(List(1,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      nameFieldValues(nameField.withKey(RowHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (filtered), 1 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1))
    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map((List(1), List(0)) -> 180))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (filtered)") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(genderField))
    )

    val expectedColHeaderValues = List(Set(List(0,1)))
    val expectedData = List(Map((List[Int](), List(0,1)) -> 180))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(ColumnHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column (same, same filter)") {
    val genderField1 = GenderField.withSingleFilter(F)
    val genderField2 = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(genderField1, genderField2))
    )

    val expectedColHeaderValues = List(Set(List(0,1)), Set(List(0,1)))
    val expectedData = List(
      Map((List[Int](), List(0,1)) -> 180),
      Map((List[Int](), List(0,1)) -> 180)
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField1.withKey(ColumnHeaderFieldKey(1))) ++
      genderFieldValues(genderField2.withKey(ColumnHeaderFieldKey(2))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column (same, different filter)") {
    val genderField1 = GenderField.withSingleFilter(M)
    val genderField2 = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(genderField1, genderField2))
    )

    val expectedColHeaderValues = List(Set(List(0,2)), Set(List(0,1)))
    val expectedData = List(
      Map((List[Int](), List(0,2)) -> 245),
      Map((List[Int](), List(0,1)) -> 180)
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField1.withKey(ColumnHeaderFieldKey(1))) ++
      genderFieldValues(genderField2.withKey(ColumnHeaderFieldKey(2))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (filtered), 1 measure, 1 column (filtered)") {
    val genderField = GenderField.withSingleFilter(F)
    val locationField = LocationField.withSingleFilter(Manchester)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(locationField)))

    val expectedRowHeaderValues = Set(List(1))
    val expectedColHeaderValues = List(Set(List(0,2)))
    val expectedData = List(Map((List(1), List(0,2)) -> 130))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      Map(locationField.withKey(ColumnHeaderFieldKey(1)) -> List(1,2)) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (multiple filters) 1 measure, 0 column") {
    val nameFilter = RetainFilter[String](Set(Laura, Nick, Ally))
    val nameField = NameField.withFilter(nameFilter)
    val tableState = TableState.Blank.withRowHeaderFields(List(nameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(2),List(4),List(6))
    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(
      Map((List(2), List(0)) -> 60, (List(4), List(0)) -> 80, (List(6), List(0)) -> 75)
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(nameField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 1 filter (not filtered)") {
    val tableState = TableState.Blank.withFilterFields(List(GenderField))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(GenderField.withKey(FilterFieldKey(0)))

    check(tableState, EmptySet, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (not filtered), 0 measure, 0 column, 1 filter (filtered)") {
    val genderField = GenderField.withSingleFilter(M)
    val tableState = TableState.Blank.withFilterFields(List(genderField)).withRowHeaderFields(List(NameField))
    val expectedRowHeaderValues = Set(List(1),List(2),List(3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Nick, Paul, Ally)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(FilterFieldKey(0))) ++
      Map(NameField.withKey(RowHeaderFieldKey(0)) -> List(1,2,3))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }
}
