package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class UnfilteredArrayTableDataSourceFilteredTest extends FunSuite {
  test("1 row (filtered), 0 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))

    val expectedRowHeaderValues = Set(List(1))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (filtered), 1 measure, 0 column") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1))
    val columnHeaderPath = new ColumnHeaderPath(0, Array(0))
    def dp(rowHeaderValues:Int*) = new DataPath(rowHeaderValues.toArray, columnHeaderPath)
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(dp(1) -> 180)
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (filtered)") {
    val genderField = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(genderField))
    )

    val columnHeaderPath = new ColumnHeaderPath(0, Array(0,1))
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(new DataPath(Array.empty, columnHeaderPath) -> 180)
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(ColumnHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column (same, same filter)") {
    val genderField1 = GenderField.withSingleFilter(F)
    val genderField2 = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(genderField1, genderField2))
    )

    val columnHeaderPath1 = new ColumnHeaderPath(0, Array(0,1))
    val columnHeaderPath2 = new ColumnHeaderPath(1, Array(0,1))
    val expectedColHeaderPaths = Set(columnHeaderPath1, columnHeaderPath2)

    val expectedData = Map(
      new DataPath(Array.empty, columnHeaderPath1) -> 180,
      new DataPath(Array.empty, columnHeaderPath2) -> 180
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField1.withKey(ColumnHeaderFieldKey(1))) ++
      genderFieldValues(genderField2.withKey(ColumnHeaderFieldKey(2))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column (same, different filter)") {
    val genderField1 = GenderField.withSingleFilter(M)
    val genderField2 = GenderField.withSingleFilter(F)
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(genderField1, genderField2))
    )

    val columnHeaderPath1 = new ColumnHeaderPath(0, Array(0,2))
    val columnHeaderPath2 = new ColumnHeaderPath(1, Array(0,1))
    val expectedColHeaderPaths = Set(columnHeaderPath1, columnHeaderPath2)

    val expectedData = Map(
      new DataPath(Array.empty, columnHeaderPath1) -> 245,
      new DataPath(Array.empty, columnHeaderPath2) -> 180
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField1.withKey(ColumnHeaderFieldKey(1))) ++
      genderFieldValues(genderField2.withKey(ColumnHeaderFieldKey(2))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptyListSet, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (filtered), 1 measure, 1 column (filtered)") {
    val genderField = GenderField.withSingleFilter(F)
    val locationField = LocationField.withSingleFilter(Manchester)
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(locationField)))

    val expectedRowHeaderValues = Set(List(1))
    val columnHeaderPath = new ColumnHeaderPath(0, Array(0,2))
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(new DataPath(Array(1), columnHeaderPath) -> 130)
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      Map(locationField.withKey(ColumnHeaderFieldKey(1)) -> List(1,2)) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (multiple filters) 1 measure, 0 column") {
    val nameFilter = RetainFilter[String](Set(Laura, Nick, Ally))
    val nameField = NameField.withFilter(nameFilter)
    val tableState = TableState.Blank.withRowHeaderFields(List(nameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(2),List(4),List(6))
    val columnHeaderPath = new ColumnHeaderPath(0, Array(0))
    def dp(rowHeaderValues:Int*) = new DataPath(rowHeaderValues.toArray, columnHeaderPath)
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(dp(2) -> 60, dp(4) -> 80, dp(6) -> 75)
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(nameField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 1 filter (not filtered)") {
    val tableState = TableState.Blank.withFilterFields(List(GenderField))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(GenderField.withKey(FilterFieldKey(0)))

    check(tableState, EmptySet, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 1 filter (filtered") {
    val genderField = GenderField.withSingleFilter(M)
    val tableState = TableState.Blank.withFilterFields(List(genderField))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(genderField.withKey(FilterFieldKey(0)))

    check(tableState, EmptySet, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 2 filter (first filtered") {
    val genderField = GenderField.withSingleFilter(M)
    val tableState = TableState.Blank.withFilterFields(List(genderField, NameField))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Nick, Paul, Ally)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(FilterFieldKey(0))) ++
      Map(NameField.withKey(FilterFieldKey(1)) -> List(1,2,3))
    check(tableState, EmptySet, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 2 filter (same, both not filtered)") {
    val genderField1 = GenderField.withKey(FilterFieldKey(0))
    val genderField2 = GenderField.withKey(FilterFieldKey(1))
    val tableState = TableState.Blank.withFilterFields(List(genderField1, genderField2))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(genderField1) ++ genderFieldValues(genderField2)

    check(tableState, EmptySet, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 2 filter (same, first filtered)") {
    val genderField1 = GenderField.withSingleFilter(M).withKey(FilterFieldKey(0))
    val genderField2 = GenderField.withKey(FilterFieldKey(1))
    val tableState = TableState.Blank.withFilterFields(List(genderField1, genderField2))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(genderField1) ++ Map(genderField2 -> List(2))

    check(tableState, EmptySet, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 3 filter (2 same, different one in the middle filtered)") {
    val nameField1 = NameField.withKey(FilterFieldKey(0))
    val genderField = GenderField.withSingleFilter(M).withKey(FilterFieldKey(1))
    val nameField2 = NameField.withKey(FilterFieldKey(2))
    val tableState = TableState.Blank.withFilterFields(List(nameField1, genderField, nameField2))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      GenderField.id -> List(GenderField.id, F, M)
    )
    val expectedFieldValues = nameFieldValues(nameField1) ++ genderFieldValues(genderField) ++ Map(nameField2 -> List(4,5,6))

    check(tableState, EmptySet, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }
}

