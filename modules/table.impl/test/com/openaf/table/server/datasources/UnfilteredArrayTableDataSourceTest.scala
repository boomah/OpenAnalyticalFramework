package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class UnfilteredArrayTableDataSourceTest extends FunSuite {
  test("1 row (key), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (key - duplicate), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField, NameField))

    val expectedRowHeaderValues = Set(List(1,1), List(2,2), List(3,3), List(4,4), List(5,5), List(6,6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      nameFieldValues(NameField.withKey(RowHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val columnHeaderPath = new ColumnHeaderPath(0, Array(0))
    def dp(rowHeaderValues:Int*) = new DataPath(rowHeaderValues.toArray, columnHeaderPath)
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(
      dp(1) -> 50,
      dp(2) -> 60,
      dp(3) -> 70,
      dp(4) -> 80,
      dp(5) -> 90,
      dp(6) -> 75
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, LocationField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(2,1), List(2,2), List(2,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(2,1), List(2,2), List(2,3))
    val columnHeaderPath = new ColumnHeaderPath(0, Array(0))
    def dp(rowHeaderValues:Int*) = new DataPath(rowHeaderValues.toArray, columnHeaderPath)
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(
      dp(1,1) -> 50,
      dp(1,2) -> 130,
      dp(2,1) -> 80,
      dp(2,2) -> 90,
      dp(2,3) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedColHeaderPaths = (1 to 6).map(i => new ColumnHeaderPath(0, Array(i))).toSet
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptySet, expectedColHeaderPaths, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(GenderField, LocationField), Nil))

    val genderField = GenderField.withKey(ColumnHeaderFieldKey(0))
    val locationField = LocationField.withKey(ColumnHeaderFieldKey(1))

    val expectedColHeaderPaths = Set(
      new ColumnHeaderPath(0, Array(1)),
      new ColumnHeaderPath(0, Array(2)),
      new ColumnHeaderPath(1, Array(1)),
      new ColumnHeaderPath(1, Array(2)),
      new ColumnHeaderPath(1, Array(3))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = genderFieldValues(genderField) ++ locationFieldValues(locationField)

    check(tableState, EmptySet, expectedColHeaderPaths, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val nameField = NameField.withKey(ColumnHeaderFieldKey(1))

    val columnHeaderPaths = (1 to 6).map(i => new ColumnHeaderPath(0, Array(0,i))).toArray
    def dp(i:Int) = new DataPath(Array.empty, columnHeaderPaths(i))
    val expectedColHeaderPaths = columnHeaderPaths.toSet
    val expectedData = Map(
      dp(0) -> 50,
      dp(1) -> 60,
      dp(2) -> 70,
      dp(3) -> 80,
      dp(4) -> 90,
      dp(5) -> 75
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(nameField) ++ scoreFieldValues(scoreField)

    check(tableState, EmptyListSet, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column") {
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(GenderField, LocationField)))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val genderField = GenderField.withKey(ColumnHeaderFieldKey(1))
    val locationField = LocationField.withKey(ColumnHeaderFieldKey(2))

    val columnHeaderPaths1 = (1 to 2).map(i => new ColumnHeaderPath(0, Array(0,i)))
    val columnHeaderPaths2 = (1 to 3).map(i => new ColumnHeaderPath(1, Array(0,i)))

    def dp1(i:Int) = new DataPath(Array.empty, columnHeaderPaths1(i))
    def dp2(i:Int) = new DataPath(Array.empty, columnHeaderPaths2(i))
    val expectedColHeaderPaths = columnHeaderPaths1.toSet ++ columnHeaderPaths2.toSet

    val expectedData = Map(
      dp1(0) -> 180,
      dp1(1) -> 245,
      dp2(0) -> 130,
      dp2(1) -> 220,
      dp2(2) -> 75
    )

    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField) ++ locationFieldValues(locationField) ++ scoreFieldValues(scoreField)

    check(tableState, EmptyListSet, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val columnHeaderPaths = (1 to 6).map(i => new ColumnHeaderPath(0, Array(0,i)))
    def dp(row:Int, colIndex:Int) = new DataPath(Array(row), columnHeaderPaths(colIndex))
    val expectedColHeaderPaths = columnHeaderPaths.toSet
    val expectedData = Map(
      dp(1,0) -> 50,
      dp(1,1) -> 60,
      dp(1,2) -> 70,
      dp(2,3) -> 80,
      dp(2,4) -> 90,
      dp(2,5) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField.withKey(ColumnHeaderFieldKey(1))) ++
      genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 2 measure, 1 column under measure 1") {
    val ageField = AgeField.copy(fieldType = Measure)
    val columnHeaderLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(ScoreField), List(LocationField)),
      ColumnHeaderTree(ageField)
    ))
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField)).withColumnHeaderLayout(columnHeaderLayout)

    val expectedRowHeaderValues = Set(List(1), List(2))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val locationField = LocationField.withKey(ColumnHeaderFieldKey(1))
    val ageFieldWithKey = ageField.withKey(ColumnHeaderFieldKey(2))

    val columnHeaderPaths1 = (1 to 3).map(i => new ColumnHeaderPath(0, Array(0,i)))
    val columnHeaderPaths2 = Array(new ColumnHeaderPath(1, Array(0)))

    def dp1(row:Int,i:Int) = new DataPath(Array(row), columnHeaderPaths1(i))
    def dp2(row:Int) = new DataPath(Array(row), columnHeaderPaths2(0))
    val expectedColHeaderPaths = columnHeaderPaths1.toSet ++ columnHeaderPaths2.toSet

    val expectedData = Map(
      dp1(1,0) -> 50,
      dp1(1,1) -> 130,
      dp1(2,0) -> 80,
      dp1(2,1) -> 90,
      dp1(2,2) -> 75,
      dp2(1)   -> 103,
      dp2(2)   -> 100
    )

    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id),
      AgeField.id -> List(AgeField.id)
    )
    val expectedFieldValues = locationFieldValues(locationField) ++
      genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField) ++
      measureFieldValues(ageFieldWithKey)

    check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 0 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val columnHeaderPath = new ColumnHeaderPath(0, Array(0))
    val expectedColHeaderPaths = Set(columnHeaderPath)
    val expectedData = Map(new DataPath(Array.empty, columnHeaderPath) -> 425)
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))
    val expectedFieldValues = scoreFieldValues(scoreField)

    check(tableState, EmptyListSet, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 2 measure, 2 column (1 under each measure)") {
    val ageField = AgeField.copy(fieldType = Measure)
    val columnHeaderLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(ScoreField), List(LocationField)),
      ColumnHeaderTree(List(ageField), List(GenderField))
    ))
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField)).withColumnHeaderLayout(columnHeaderLayout)

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val locationField = LocationField.withKey(ColumnHeaderFieldKey(1))
    val ageFieldWithKey = ageField.withKey(ColumnHeaderFieldKey(2))
    val genderField = GenderField.withKey(ColumnHeaderFieldKey(3))

    val columnHeaderPaths1 = (1 to 3).map(i => new ColumnHeaderPath(0, Array(0,i)))
    val columnHeaderPaths2 = (1 to 2).map(i => new ColumnHeaderPath(1, Array(0,i)))

    def dp1(row:Int,i:Int) = new DataPath(Array(row), columnHeaderPaths1(i))
    def dp2(row:Int,i:Int) = new DataPath(Array(row), columnHeaderPaths2(i))
    val expectedColHeaderPaths = columnHeaderPaths1.toSet ++ columnHeaderPaths2.toSet

    val expectedData = Map(
      dp1(1,0) -> 50,
      dp1(2,1) -> 60,
      dp1(3,1) -> 70,
      dp1(4,0) -> 80,
      dp1(5,1) -> 90,
      dp1(6,2) -> 75,
      dp2(1,0) -> 36,
      dp2(2,0) -> 36,
      dp2(3,0) -> 31,
      dp2(4,1) -> 34,
      dp2(5,1) -> 32,
      dp2(6,1) -> 34
    )

    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id),
      AgeField.id -> List(AgeField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(locationField) ++ genderFieldValues(genderField) ++ scoreFieldValues(scoreField) ++
      measureFieldValues(ageFieldWithKey)

      check(tableState, expectedRowHeaderValues, expectedColHeaderPaths, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 1 filter") {
    val tableState = TableState.Blank.withFilterFields(List(NameField))

    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField.withKey(FilterFieldKey(0)))

    check(tableState, Set.empty, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 2 filter") {
    val tableState = TableState.Blank.withFilterFields(List(NameField, GenderField))

    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      GenderField.id -> List(GenderField.id, F, M)
    )
    val expectedFieldValues = nameFieldValues(NameField.withKey(FilterFieldKey(0))) ++
      genderFieldValues(GenderField.withKey(FilterFieldKey(1)))

    check(tableState, Set.empty, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 2 filter (same)") {
    val nameField1 = NameField.withKey(FilterFieldKey(0))
    val nameField2 = NameField.withKey(FilterFieldKey(1))
    val tableState = TableState.Blank.withFilterFields(List(nameField1, nameField2))

    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally)
    )
    val expectedFieldValues = nameFieldValues(nameField1) ++ nameFieldValues(nameField2)

    check(tableState, Set.empty, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 0 column, 3 filter (2 same, different one in the middle)") {
    val nameField1 = NameField.withKey(FilterFieldKey(0))
    val nameField2 = NameField.withKey(FilterFieldKey(2))
    val tableState = TableState.Blank.withFilterFields(List(nameField1, GenderField, nameField2))

    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      GenderField.id -> List(GenderField.id, F, M)
    )
    val expectedFieldValues = nameFieldValues(nameField1) ++ nameFieldValues(nameField2) ++
      genderFieldValues(GenderField.withKey(FilterFieldKey(1)))

    check(tableState, Set.empty, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }
}
