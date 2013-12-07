package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class RawRowBasedTableDataSourceTest extends FunSuite {
  val dataSource = RawRowBasedTableDataSource(data, FieldIDs, Group)

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
      .withMeasureAreaLayout(MeasureAreaLayout(ScoreField))

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
      .withMeasureAreaLayout(MeasureAreaLayout(ScoreField))

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
    val tableState = TableState.Blank.withMeasureAreaLayout(MeasureAreaLayout(NameField))

    val expectedColHeaderValues = List(Set(List(1), List(2), List(3), List(4), List(5), List(6)))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))

    check(tableState, Set.empty, expectedColHeaderValues, List(Map.empty), expectedValueLookUp)
  }

  test("0 row, 0 measure, 2 column") {
    val tableState = TableState.Blank.withMeasureAreaLayout(MeasureAreaLayout(List(GenderField, LocationField), Nil))

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
    val tableState = TableState.Blank.withMeasureAreaLayout(MeasureAreaLayout(ScoreField, List(NameField)))

    val expectedColHeaderValues = List(Set(List(1,0), List(2,0), List(3,0), List(4,0), List(5,0), List(6,0)))
    val expectedData = List(Map(
      (List[Int](), List(1,0)) -> 50,
      (List[Int](), List(2,0)) -> 60,
      (List[Int](), List(3,0)) -> 70,
      (List[Int](), List(4,0)) -> 80,
      (List[Int](), List(5,0)) -> 90,
      (List[Int](), List(6,0)) -> 75
    ))
    val expectedValueLookUp = Map(
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )

    check(tableState, Set(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("0 row, 1 measure, 2 column") {
    val tableState = TableState.Blank
      .withMeasureAreaLayout(MeasureAreaLayout(ScoreField, List(GenderField, LocationField)))

    val expectedColHeaderValues = List(
      Set(List(1,0), List(2,0)),
      Set(List(1,0), List(2,0), List(3,0))
    )
    val expectedData = List(
      Map(
        (List[Int](), List(1,0)) -> 180,
        (List[Int](), List(2,0)) -> 245
      ),
      Map(
        (List[Int](), List(1,0)) -> 130,
        (List[Int](), List(2,0)) -> 220,
        (List[Int](), List(3,0)) -> 75
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
      .withMeasureAreaLayout(MeasureAreaLayout(ScoreField, List(NameField)))

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedColHeaderValues = List(Set(List(1,0), List(2,0), List(3,0), List(4,0), List(5,0), List(6,0)))
    val expectedData = List(Map(
      (List(1), List(1,0)) -> 50,
      (List(1), List(2,0)) -> 60,
      (List(1), List(3,0)) -> 70,
      (List(2), List(4,0)) -> 80,
      (List(2), List(5,0)) -> 90,
      (List(2), List(6,0)) -> 75
    ))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally),
      ScoreField.id -> List(ScoreField.id)
    )

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("1 row, 2 measure, 1 column under measure 1") {
    val measureAreaLayout = MeasureAreaLayout(List(
      MeasureAreaTree(List(ScoreField), List(LocationField)),
      MeasureAreaTree(AgeField.copy(fieldType = Measure))
    ))
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField)).withMeasureAreaLayout(measureAreaLayout)

    val expectedRowHeaderValues = Set(List(1), List(2))
    val expectedColHeaderValues = List(
      Set(List(1,0), List(2,0), List(3,0)),
      Set(List(0))
    )
    val expectedData = List(
      Map(
        (List(1), List(1,0)) -> 50,
        (List(1), List(2,0)) -> 130,
        (List(2), List(1,0)) -> 80,
        (List(2), List(2,0)) -> 90,
        (List(2), List(3,0)) -> 75
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
    val tableState = TableState.Blank.withMeasureAreaLayout(MeasureAreaLayout(ScoreField))

    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map((List[Int](), List(0)) -> 425))
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))

    check(tableState, Set(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("1 row (key), 2 measure, 2 column (1 under each measure)") {
    val measureAreaLayout = MeasureAreaLayout(List(
      MeasureAreaTree(List(ScoreField), List(LocationField)),
      MeasureAreaTree(List(AgeField.copy(fieldType = Measure)), List(GenderField))
    ))
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField)).withMeasureAreaLayout(measureAreaLayout)

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4), List(5), List(6))
    val expectedColHeaderValues = List(
      Set(List(1,0), List(2,0), List(3,0)),
      Set(List(1,0), List(2,0))
    )
    val expectedData = List(
      Map(
        (List(1), List(1,0)) -> 50,
        (List(2), List(2,0)) -> 60,
        (List(3), List(2,0)) -> 70,
        (List(4), List(1,0)) -> 80,
        (List(5), List(2,0)) -> 90,
        (List(6), List(3,0)) -> 75
      ),
      Map(
        (List(1), List(1,0)) -> 36,
        (List(2), List(1,0)) -> 36,
        (List(3), List(1,0)) -> 31,
        (List(4), List(2,0)) -> 34,
        (List(5), List(2,0)) -> 32,
        (List(6), List(2,0)) -> 34
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
    assert(result.pathData.map(_.data).toList === expectedData)
    assert(result.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
  }
}
