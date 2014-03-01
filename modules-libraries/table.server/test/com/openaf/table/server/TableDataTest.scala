package com.openaf.table.server

import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._
import com.openaf.table.server.datasources.RawRowBasedTableDataSource
import com.openaf.table.lib.api.TableState

class TableDataTest extends FunSuite {
  val dataSource = RawRowBasedTableDataSource(data, FieldIDs, Group)
  
  test("test rowHeaderAsString 1 row") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField))
    val expectedText =
      """┌─┐
        |│F│
        |├─┤
        |│M│
        |└─┘""".stripMargin

    assert(tableData(tableState).rowHeadersAsString === expectedText)
  }

  test("test rowHeaderAsString 2 row") {
    val tableState = TableState.Blank.withRowHeaderFields(List(GenderField, LocationField))
    val expectedText =
      """┌─┬──────────┐
        |│F│London    │
        |├─┼──────────┤
        |│F│Manchester│
        |├─┼──────────┤
        |│M│Edinburgh │
        |├─┼──────────┤
        |│M│London    │
        |├─┼──────────┤
        |│M│Manchester│
        |└─┴──────────┘""".stripMargin

    assert(tableData(tableState).rowHeadersAsString === expectedText)
  }
  
  private def tableData(tableState:TableState) = TableDataGenerator.tableData(tableState, dataSource)
}
