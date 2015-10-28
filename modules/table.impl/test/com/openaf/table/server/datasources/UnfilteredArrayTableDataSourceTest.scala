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
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1)(ch) -> 50,
      p(2)(ch) -> 60,
      p(3)(ch) -> 70,
      p(4)(ch) -> 80,
      p(5)(ch) -> 90,
      p(6)(ch) -> 75
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
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
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,1)(ch) -> 50,
      p(1,2)(ch) -> 130,
      p(2,1)(ch) -> 80,
      p(2,2)(ch) -> 90,
      p(2,3)(ch) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedColHeaders = (1 to 6).map(i => List(i,0)).toSet
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptySet, expectedColHeaders, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(GenderField, LocationField), Nil))

    val genderField = GenderField.withKey(ColumnHeaderFieldKey(0))
    val locationField = LocationField.withKey(ColumnHeaderFieldKey(1))

    val expectedColHeaders = Set(
      List(1,0),
      List(2,0),
      List(1,1),
      List(2,1),
      List(3,1)
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = genderFieldValues(genderField) ++ locationFieldValues(locationField)

    check(tableState, EmptySet, expectedColHeaders, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val nameField = NameField.withKey(ColumnHeaderFieldKey(1))

    val chs = (1 to 6).map(i => List(0,i,0)).toArray
    def dp(i:Int) = (Array.empty, chs(i))
    val expectedColHeaders = chs.toSet
    val expectedData = Map(
      p()(chs(0)) -> 50,
      p()(chs(1)) -> 60,
      p()(chs(2)) -> 70,
      p()(chs(3)) -> 80,
      p()(chs(4)) -> 90,
      p()(chs(5)) -> 75
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(nameField) ++ scoreFieldValues(scoreField)

    check(tableState, EmptyListSet, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column") {
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(GenderField, LocationField)))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val genderField = GenderField.withKey(ColumnHeaderFieldKey(1))
    val locationField = LocationField.withKey(ColumnHeaderFieldKey(2))

    val chs1 = (1 to 2).map(i => List(0,i,0))
    val chs2 = (1 to 3).map(i => List(0,i,1))

    val expectedColHeaders = chs1.toSet ++ chs2.toSet

    val expectedData = Map(
      p()(chs1(0)) -> 180,
      p()(chs1(1)) -> 245,
      p()(chs2(0)) -> 130,
      p()(chs2(1)) -> 220,
      p()(chs2(2)) -> 75
    )

    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField) ++ locationFieldValues(locationField) ++ scoreFieldValues(scoreField)

    check(tableState, EmptyListSet, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val chs = (1 to 6).map(i => List(0,i,0))
    val expectedColHeaders = chs.toSet
    val expectedData = Map(
      p(1)(chs(0)) -> 50,
      p(1)(chs(1)) -> 60,
      p(1)(chs(2)) -> 70,
      p(2)(chs(3)) -> 80,
      p(2)(chs(4)) -> 90,
      p(2)(chs(5)) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField.withKey(ColumnHeaderFieldKey(1))) ++
      genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
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

    val chs1 = (1 to 3).map(i => List(0,i,0))
    val chs2 = Array(List(0,1))

    val expectedColHeaders = chs1.toSet ++ chs2.toSet

    val expectedData = Map(
      p(1)(chs1(0)) -> 50,
      p(1)(chs1(1)) -> 130,
      p(2)(chs1(0)) -> 80,
      p(2)(chs1(1)) -> 90,
      p(2)(chs1(2)) -> 75,
      p(1)(chs2(0)) -> 103,
      p(2)(chs2(0)) -> 100
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

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 0 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(p()(ch) -> 425)
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))
    val expectedFieldValues = scoreFieldValues(scoreField)

    check(tableState, EmptyListSet, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
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

    val chs1 = (1 to 3).map(i => List(0,i,0))
    val chs2 = (1 to 2).map(i => List(0,i,1))

    val expectedColHeaders = chs1.toSet ++ chs2.toSet

    val expectedData = Map(
      p(1)(chs1(0)) -> 50,
      p(2)(chs1(1)) -> 60,
      p(3)(chs1(1)) -> 70,
      p(4)(chs1(0)) -> 80,
      p(5)(chs1(1)) -> 90,
      p(6)(chs1(2)) -> 75,
      p(1)(chs2(0)) -> 36,
      p(2)(chs2(0)) -> 36,
      p(3)(chs2(0)) -> 31,
      p(4)(chs2(1)) -> 34,
      p(5)(chs2(1)) -> 32,
      p(6)(chs2(1)) -> 34
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

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
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

  test("1 row, 1 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val ch = List(0,0)
    val expectedColHeaders = Set(List(0,0))
    val expectedData = Map(
      p(1)(ch) -> 180,
      p(2)(ch) -> 245
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 1 measure, 0 column, average") {
    val scoreField = ScoreField.withKey(ColumnHeaderFieldKey(0)).withCombinerType(Average)
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(scoreField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val ch = List(0,0)
    val expectedColHeaders = Set(List(0,0))
    val expectedData = Map(
      p(1)(ch) -> 60,
      p(2)(ch) -> 81
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(scoreField.id)
    )
    val expectedFieldValues = genderFieldValues(GenderField.withKey(RowHeaderFieldKey(0))) ++
      scoreFieldValues(scoreField)

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  // TODO - add another test that checks Average with filters
}
