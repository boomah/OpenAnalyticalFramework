package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class RawRowBasedTableDataSourceTest extends FunSuite {
  test("1 row (key), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (key - duplicate), 0 measure, 0 column") {
    val nameField2 = NameField.duplicate
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField, nameField2))

    val expectedRowHeaderValues = Set(List(1,1), List(2,2), List(3,3), List(4,4), List(5,5), List(6,6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField) ++ nameFieldValues(nameField2)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))
    val expectedFieldValues = genderFieldValues(GenderField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(NameField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map(
      (List(1), List(0)) -> 50,
      (List(2), List(0)) -> 60,
      (List(3), List(0)) -> 70,
      (List(4), List(0)) -> 80,
      (List(5), List(0)) -> 90,
      (List(6), List(0)) -> 75
    ))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, LocationField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(2,1), List(2,2), List(2,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = locationFieldValues(LocationField) ++ genderFieldValues(GenderField)

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row, 1 measure, 0 column") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(2,1), List(2,2), List(2,3))
    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map(
      (List(1,1), List(0)) -> 50,
      (List(1,2), List(0)) -> 130,
      (List(2,1), List(0)) -> 80,
      (List(2,2), List(0)) -> 90,
      (List(2,3), List(0)) -> 75
    ))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = locationFieldValues(LocationField) ++ genderFieldValues(GenderField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedColHeaderValues = List(Set(List(1), List(2), List(3), List(4), List(5), List(6)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))
    val expectedFieldValues = nameFieldValues(NameField)

    check(tableState, EmptySet, expectedColHeaderValues, EmptyMapList, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(GenderField, LocationField), Nil))

    val expectedColHeaderValues = List(
      Set(List(1), List(2)),
      Set(List(1), List(2), List(3))
    )
    val expectedData:List[Map[(List[Int],List[Int]),Int]] = List(Map.empty, Map.empty)
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )
    val expectedFieldValues = locationFieldValues(LocationField) ++ genderFieldValues(GenderField)

    check(tableState, EmptySet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val expectedColHeaderValues = List(Set(List(0,1), List(0,2), List(0,3), List(0,4), List(0,5), List(0,6)))
    val expectedData = List(Map(
      (List[Int](), List(0,1)) -> 50,
      (List[Int](), List(0,2)) -> 60,
      (List[Int](), List(0,3)) -> 70,
      (List[Int](), List(0,4)) -> 80,
      (List[Int](), List(0,5)) -> 90,
      (List[Int](), List(0,6)) -> 75
    ))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField) ++ ScoreFieldValues

    check(tableState, EmptyListSet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column") {
    val tableState = TableState.Blank
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(GenderField, LocationField)))

    val expectedColHeaderValues = List(
      Set(List(0,1), List(0,2)),
      Set(List(0,1), List(0,2), List(0,3))
    )
    val expectedData = List(
      Map(
        (List[Int](), List(0,1)) -> 180,
        (List[Int](), List(0,2)) -> 245
      ),
      Map(
        (List[Int](), List(0,1)) -> 130,
        (List[Int](), List(0,2)) -> 220,
        (List[Int](), List(0,3)) -> 75
      )
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = locationFieldValues(LocationField) ++ genderFieldValues(GenderField) ++ ScoreFieldValues

    check(tableState, EmptyListSet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 1 measure, 1 column (key)") {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(GenderField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField, List(NameField)))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedColHeaderValues = List(Set(List(0,1), List(0,2), List(0,3), List(0,4), List(0,5), List(0,6)))
    val expectedData = List(Map(
      (List(1), List(0,1)) -> 50,
      (List(1), List(0,2)) -> 60,
      (List(1), List(0,3)) -> 70,
      (List(2), List(0,4)) -> 80,
      (List(2), List(0,5)) -> 90,
      (List(2), List(0,6)) -> 75
    ))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField) ++ genderFieldValues(GenderField) ++ ScoreFieldValues

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row, 2 measure, 1 column under measure 1") {
    val ageField = AgeField.copy(fieldType = Measure)
    val columnHeaderLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(ScoreField), List(LocationField)),
      ColumnHeaderTree(ageField)
    ))
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField)).withColumnHeaderLayout(columnHeaderLayout)

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedColHeaderValues = List(
      Set(List(0,1), List(0,2), List(0,3)),
      Set(List(0))
    )
    val expectedData = List(
      Map(
        (List(1), List(0,1)) -> 50,
        (List(1), List(0,2)) -> 130,
        (List(2), List(0,1)) -> 80,
        (List(2), List(0,2)) -> 90,
        (List(2), List(0,3)) -> 75
      ),
      Map(
        (List(1), List(0)) -> 103,
        (List(2), List(0)) -> 100
      )
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id),
      AgeField.id -> List(AgeField.id)
    )
    val expectedFieldValues = locationFieldValues(LocationField) ++ genderFieldValues(GenderField) ++
      ScoreFieldValues ++ measureFieldValues(ageField)

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 1 measure, 0 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map((List[Int](), List(0)) -> 425))
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))
    val expectedFieldValues = ScoreFieldValues

    check(tableState, EmptyListSet, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (key), 2 measure, 2 column (1 under each measure)") {
    val ageField = AgeField.copy(fieldType = Measure)
    val columnHeaderLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(ScoreField), List(LocationField)),
      ColumnHeaderTree(List(ageField), List(GenderField))
    ))
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField)).withColumnHeaderLayout(columnHeaderLayout)

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val expectedColHeaderValues = List(
      Set(List(0,1), List(0,2), List(0,3)),
      Set(List(0,1), List(0,2))
    )
    val expectedData = List(
      Map(
        (List(1), List(0,1)) -> 50,
        (List(2), List(0,2)) -> 60,
        (List(3), List(0,2)) -> 70,
        (List(4), List(0,1)) -> 80,
        (List(5), List(0,2)) -> 90,
        (List(6), List(0,3)) -> 75
      ),
      Map(
        (List(1), List(0,1)) -> 36,
        (List(2), List(0,1)) -> 36,
        (List(3), List(0,1)) -> 31,
        (List(4), List(0,2)) -> 34,
        (List(5), List(0,2)) -> 32,
        (List(6), List(0,2)) -> 34
      )
    )
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id),
      AgeField.id -> List(AgeField.id)
    )
    val expectedFieldValues = nameFieldValues(NameField) ++ locationFieldValues(LocationField) ++
      genderFieldValues(GenderField) ++ ScoreFieldValues ++ measureFieldValues(ageField)

      check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedFieldValues, expectedValueLookUp)
  }
}
