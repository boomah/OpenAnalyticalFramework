package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._

class UnfilteredArrayTableDataSourceTransformedTest extends FunSuite {
  test("1 row (transformed to Double), 0 measure, 0 column") {
    val ageField = AgeField.withTransformerType(IntToDoubleTransformerType)
    val tableState = TableState.Blank.withRowHeaderFields(List(ageField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4))
    // This is not a very good test because Int and Doubles equal each other in this case so it could be that the
    // transform hasn't occurred
    val expectedValueLookUp = Map(AgeField.id -> List(AgeField.id, 36.0, 31.0, 34.0, 32))
    val expectedFieldValues = ageFieldValues(ageField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("1 row (transformed to MonthYear), 0 measure, 0 column") {
    val dateField = DateField.withTransformerType(LocalDateToYearMonthTransformerType)
    val tableState = TableState.Blank.withRowHeaderFields(List(dateField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4))
    val expectedValueLookUp = Map(DateField.id -> List(DateField.id, MonthYear1, MonthYear2, MonthYear3, MonthYear4))
    val expectedFieldValues = monthYearFieldValues(dateField.withKey(RowHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)
  }

  test("0 row, 0 measure, 1 column (transformed to MonthYear)") {
    val dateField = DateField.withTransformerType(LocalDateToYearMonthTransformerType)
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(dateField))


    val expectedColHeaders = (1 to 4).map(i => List(i,0)).toSet
    val expectedValueLookUp = Map(DateField.id -> List(DateField.id, MonthYear1, MonthYear2, MonthYear3, MonthYear4))
    val expectedFieldValues = monthYearFieldValues(dateField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, EmptySet, expectedColHeaders, Map.empty, expectedFieldValues, expectedValueLookUp)
  }
}
