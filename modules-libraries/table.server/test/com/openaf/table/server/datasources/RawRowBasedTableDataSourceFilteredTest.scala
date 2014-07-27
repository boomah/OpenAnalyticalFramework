package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._

class RawRowBasedTableDataSourceFilteredTest extends FunSuite {
  val dataSource = RawRowBasedTableDataSource(data, FieldIDs, Groups)

  test("1 row (filtered), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField.filterSingleValue(F)))

    val expectedRowHeaderValues = Set(List(1))
    val expectedValueLookUp = Map(GenderField.id -> List(GenderField.id, F, M))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("2 row (1st filtered), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField.filterSingleValue(F), NameField))

    val expectedRowHeaderValues = Set(List(1,1), List(1,2), List(1,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie)
    )

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("2 row (1st filtered 2nd value), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField.filterSingleValue(M), NameField))

    val expectedRowHeaderValues = Set(List(2,1), List(2,2), List(2,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Nick, Paul, Ally)
    )

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("2 row (2nd filtered), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, NameField.filterSingleValue(Josie)))

    val expectedRowHeaderValues = Set(List(1,3))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally)
    )

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("1 row (filtered), 1 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField.filterSingleValue(F)))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1))
    val expectedColHeaderValues = List(Set(List(0)))
    val expectedData = List(Map((List(1), List(0)) -> 180))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )

    check(tableState, expectedRowHeaderValues, expectedColHeaderValues, expectedData, expectedValueLookUp)
  }

  test("0 row, 1 measure, 1 column (filtered)") {
    val tableState = TableState.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(ScoreField, List(GenderField.filterSingleValue(F)))
    )

    val expectedColHeaderValues = List(Set(List(0,1)))
    val expectedData = List(Map((List[Int](), List(0,1)) -> 180))
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      ScoreField.id -> List(ScoreField.id)
    )

    check(tableState, Set(Nil), expectedColHeaderValues, expectedData, expectedValueLookUp)
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
