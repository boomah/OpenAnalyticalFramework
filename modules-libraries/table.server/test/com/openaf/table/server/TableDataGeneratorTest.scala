package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._
import com.openaf.table.server.datasources.RawRowBasedTableDataSource
import com.openaf.table.lib.api._

class TableDataGeneratorTest extends FunSuite {
  val dataSource = RawRowBasedTableDataSource(data, FieldIDs, Groups)

  test("1 row (key), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField))

    val expectedRowHeaderValues = List(List(6), List(3), List(2), List(4), List(5), List(1))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (key, reversed), 0 measure, 0 column") {
    val nameField = NameField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(nameField))

    val expectedRowHeaderValues = List(List(1), List(5), List(4), List(2), List(3), List(6))
    val expectedValueLookUp = Map(nameField.id -> List(nameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = reversedNameFieldValues(nameField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))

    val expectedRowHeaderValues = List(List(1), List(2))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = orderedGenderFieldValues(GenderField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (reversed), 0 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField))

    val expectedRowHeaderValues = List(List(2), List(1))
    val expectedValueLookUp = Map(genderField.id -> List(genderField.id, F, M))
    val expectedFieldValues = reversedGenderFieldValues(genderField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (key), 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = List(List(6), List(3), List(2), List(4), List(5), List(1))
    val expectedColHeaderValues = List(List(List(0)))
    val expectedData = List(List(List(75), List(70), List(60), List(80), List(90), List(50)))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("2 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, LocationField))

    val expectedRowHeaderValues = List(List(1,1), List(1,2), List(2,3), List(2,1), List(2,2))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ orderedLocationFieldValues(LocationField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("2 row (row 1 reversed), 0 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(genderField, LocationField))

    val expectedRowHeaderValues = List(List(2,3), List(2,1), List(2,2), List(1,1), List(1,2))
    val expectedValueLookUp = Map(
      genderField.id -> List(genderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = reversedGenderFieldValues(genderField) ++ orderedLocationFieldValues(LocationField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("2 row (row 2 reversed), 0 measure, 0 column") {
    val locationField = LocationField.flipSortOrder
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, locationField))

    val expectedRowHeaderValues = List(List(1,2), List(1,1), List(2,2), List(2,1), List(2,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      locationField.id -> List(locationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ reversedLocationFieldValues(locationField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp, expectedFieldValues)
  }

  test("2 row, 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = List(List(1,1), List(1,2), List(2,3), List(2,1), List(2,2))
    val expectedColHeaderValues = List(List(List(0)))
    val expectedData = List(List(List(50), List(130), List(75), List(80), List(90)))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ orderedLocationFieldValues(LocationField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("2 row (row 1 reversed), 1 measure, 0 column") {
    val genderField = GenderField.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = List(List(2,3), List(2,1), List(2,2), List(1,1), List(1,2))
    val expectedColHeaderValues = List(List(List(0)))
    val expectedData = List(List(List(75), List(80), List(90), List(50), List(130)))
    val expectedValueLookUp = Map(
      genderField.id -> List(genderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = reversedGenderFieldValues(genderField) ++ orderedLocationFieldValues(LocationField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("2 row (row 2 reversed), 1 measure, 0 column") {
    val locationField = LocationField.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, locationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = List(List(1,2), List(1,1), List(2,2), List(2,1), List(2,3))
    val expectedColHeaderValues = List(List(List(0)))
    val expectedData = List(List(List(130), List(50), List(90), List(80), List(75)))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ reversedLocationFieldValues(locationField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 0 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedColHeaderValues = List(List(List(6), List(3), List(2), List(4), List(5), List(1)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField)

    check(tableState, Nil, expectedColHeaderValues, List(Nil), expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 0 measure, 1 column (key, reversed)") {
    val nameField = NameField.flipSortOrder
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(nameField))

    val expectedColHeaderValues = List(List(List(1), List(5), List(4), List(2), List(3), List(6)))
    val expectedValueLookUp = Map(nameField.id -> List(nameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = reversedNameFieldValues(nameField)

    check(tableState, Nil, expectedColHeaderValues, List(Nil), expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val expectedColHeaderValues = List(List(List(0,6), List(0,3), List(0,2), List(0,4), List(0,5), List(0,1)))
    val expectedData = List(List(List(75, 70, 60, 80, 90, 50)))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField) ++ ScoreFieldValues

    check(tableState, List(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 1 measure, 1 column (right of measure)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(ScoreField, GenderField), Nil))

    val expectedColHeaderValues = List(List(List(0)), List(List(1), List(2)))
    val expectedData = List(List(List(425)), List(List(NoValue, NoValue)))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ ScoreFieldValues

    check(tableState, List(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 1 measure, 1 column (left of measure)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(GenderField, ScoreField), Nil))

    val expectedColHeaderValues = List(List(List(1), List(2)), List(List(0)))
    val expectedData = List(List(List(NoValue, NoValue)), List(List(425)))
    val expectedValueLookUp = Map(
      ScoreField.id -> List(ScoreField.id),
      GenderField.id -> List(GenderField.id, F, M)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ ScoreFieldValues

    check(tableState, List(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (key), 0 measure, 1 column (key, same as row)") {
    val nameField = NameField.duplicate
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(nameField))

    val expectedRowHeaderValues = List(List(6), List(3), List(2), List(4), List(5), List(1))
    val expectedColHeaderValues = List(List(List(6), List(3), List(2), List(4), List(5), List(1)))
    val expectedData = List(List.fill(6)(List.fill(6)(NoValue)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(nameField) ++ orderedNameFieldValues(NameField)

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (key, reversed), 0 measure, 1 column (key, same as row)") {
    val nameField1 = NameField.flipSortOrder
    val nameField2 = NameField.duplicate
    val tableState = TableState.Blank
      .withRowHeaderFields(List(nameField1))
      .withColumnHeaderLayout(ColumnHeaderLayout(nameField2))

    val expectedRowHeaderValues = List(List(1), List(5), List(4), List(2), List(3), List(6))
    val expectedColHeaderValues = List(List(List(6), List(3), List(2), List(4), List(5), List(1)))
    val expectedData = List(List.fill(6)(List.fill(6)(NoValue)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = reversedNameFieldValues(nameField1) ++ orderedNameFieldValues(nameField2)

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (key), 0 measure, 1 column (key, same as row but reversed)") {
    val nameField = NameField.duplicate.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(nameField))

    val expectedRowHeaderValues = List(List(6), List(3), List(2), List(4), List(5), List(1))
    val expectedColHeaderValues = List(List(List(1), List(5), List(4), List(2), List(3), List(6)))
    val expectedData = List(List.fill(6)(List.fill(6)(NoValue)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = orderedNameFieldValues(NameField) ++ reversedNameFieldValues(nameField)

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("1 row (key), 1 measure, 1 column (key, same as row but reversed)") {
    val nameField = NameField.duplicate.flipSortOrder
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(nameField)))

    val expectedRowHeaderValues = List(List(6), List(3), List(2), List(4), List(5), List(1))
    val expectedColHeaderValues = List(List(List(0,1), List(0,5), List(0,4), List(0,2), List(0,3), List(0,6)))
    val expectedData = List(List(
      List(NoValue, NoValue, NoValue, NoValue, NoValue, 75     ),
      List(NoValue, NoValue, NoValue, NoValue, 70,      NoValue),
      List(NoValue, NoValue, NoValue, 60,      NoValue, NoValue),
      List(NoValue, NoValue, 80,      NoValue, NoValue, NoValue),
      List(NoValue, 90,      NoValue, NoValue, NoValue, NoValue),
      List(50,      NoValue, NoValue, NoValue, NoValue, NoValue)
    ))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = orderedNameFieldValues(NameField) ++ reversedNameFieldValues(nameField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 0 measure, 2 column (one under the other)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(GenderField, List(LocationField)))

    val expectedColHeaderValues = List(
      List(List(1,1), List(1,2), List(2,3), List(2,1), List(2,2))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ orderedLocationFieldValues(LocationField)

    check(tableState, Nil, expectedColHeaderValues, List(Nil), expectedValueLookUp, expectedFieldValues)
  }

  test("0 row, 0 measure, 2 column (one under the other and reversed)") {
    val locationField = LocationField.flipSortOrder
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(GenderField, List(locationField)))

    val expectedColHeaderValues = List(
      List(List(1,2), List(1,1), List(2,2), List(2,1), List(2,3))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = orderedGenderFieldValues(GenderField) ++ reversedLocationFieldValues(locationField)

    check(tableState, Nil, expectedColHeaderValues, List(Nil), expectedValueLookUp, expectedFieldValues)
  }

  private def check(tableState:TableState, expectedRowHeaderValues:List[List[Int]],
                    expectedColHeaderValues:List[List[List[Int]]],
                    expectedData:List[List[List[Any]]],
                    expectedValueLookUp:Map[FieldID,List[Any]],
                    expectedFieldValues:Map[Field[_],List[Int]]=Map.empty) {
    val tableData = TableDataGenerator.tableData(tableState, dataSource)

    assert(tableData.tableValues.rowHeaders.map(_.toList).toList === expectedRowHeaderValues)
    assert(tableData.tableValues.columnHeaders.map(_.map(_.toList).toList).toList === expectedColHeaderValues)
    assert(tableData.tableValues.data.map(_.map(_.toList).toList).toList === expectedData)
    assert(tableData.tableValues.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
    assert(tableData.tableValues.fieldValues.values.mapValues(_.toList) === expectedFieldValues)
  }
}
