package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class RawRowBasedTableDataSourceTest extends FunSuite {
  val dataSource = RawRowBasedTableDataSource(data, FieldIDs, Groups)

  test("1 row (key), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("2 row (key - duplicate), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField, NameField))

    val expectedRowHeaderValues = Set(List(1,1), List(2,2), List(3,3), List(4,4), List(5,5), List(6,6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("1 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("2 row, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, LocationField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(2,1), List(2,2), List(2,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (key)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(NameField))

    val expectedColHeaderValues = List(Set(List(1), List(2), List(3), List(4), List(5), List(6)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))

    check(tableState, Set.empty, expectedColHeaderValues, List(Map.empty), expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(List(GenderField, LocationField), Nil))

    val expectedColHeaderValues = List(
      Set(List(1), List(2)),
      Set(List(1), List(2), List(3))
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh)
    )

    check(tableState, Set.empty, expectedColHeaderValues, List(Map.empty, Map.empty), expectedValueLookUp)
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

    check(tableState, Set(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp)
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

    check(tableState, Set(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp)
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

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("1 row, 2 measure, 1 column under measure 1") {
    val columnHeaderLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(ScoreField), List(LocationField)),
      ColumnHeaderTree(AgeField.copy(fieldType = Measure))
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

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("0 row, 1 measure, 0 column") {
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map((List[Int](), List(0)) -> 425))
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))

    check(tableState, Set(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("1 row (key), 2 measure, 2 column (1 under each measure)") {
    val columnHeaderLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(ScoreField), List(LocationField)),
      ColumnHeaderTree(List(AgeField.copy(fieldType = Measure)), List(GenderField))
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

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  private def check(tableState:TableState, expectedRowHeaderValues:Set[List[Int]],
                    expectedColHeaderValues:List[Set[List[Int]]], expectedData:List[Map[(List[Int],List[Int]),Int]],
                    expectedValueLookUp:Map[FieldID,List[Any]]) {
    val result = dataSource.result(tableState)
    assert(result.rowHeaderValues.map(_.toList).toSet === expectedRowHeaderValues)
    assert(result.pathData.map(_.colHeaderValues.map(_.toList).toSet).toList === expectedColHeaderValues)
    val convertedData = result.pathData.map(_.data.map{case (key,value) => (key.array1.toList, key.array2.toList) -> value}).toList
    assert(convertedData === expectedData)
    assert(result.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
  }
}
