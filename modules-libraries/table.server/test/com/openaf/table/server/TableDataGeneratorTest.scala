package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._
import com.openaf.table.server.datasources.RawRowBasedTableDataSource
import com.openaf.table.lib.api.{MeasureAreaLayout, TableState}

class TableDataGeneratorTest extends FunSuite {
  val dataSource = RawRowBasedTableDataSource(data, Fields, Group)

  test("1 row (key), 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField))

    val expectedRowHeaderValues = List(List(6), List(3), List(2), List(4), List(5), List(1))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }

  test("1 row (key) reversed, 0 measure, 0 column") {
    val tableState = TableState.Blank.withRowHeaderFields(List(NameField.flipSortOrder))

    val expectedRowHeaderValues = List(List(1), List(5), List(4), List(2), List(3), List(6))
    val expectedValueLookUp = Map(NameField.id -> List(NameField.id, Rosie, Laura, Josie, Nick, Paul, Ally))

    check(tableState, expectedRowHeaderValues, Nil, Nil, expectedValueLookUp)
  }


  private def check(tableState:TableState, expectedRowHeaderValues:List[List[Int]],
                    expectedColHeaderValues:List[List[List[Int]]],
                    expectedData:List[List[List[Any]]],
                    expectedValueLookUp:Map[String,List[String]]) {
    val tableData = TableDataGenerator.tableData(tableState, dataSource)

    assert(tableData.rowHeaders.map(_.toList).toList === expectedRowHeaderValues)
    assert(tableData.columnHeaders.map(_.map(_.toList).toList).toList === expectedColHeaderValues)
    assert(tableData.data.map(_.map(_.toList).toList).toList === expectedData)
    assert(tableData.valueLookUp.mapValues(_.toList) === expectedValueLookUp)
  }
}
