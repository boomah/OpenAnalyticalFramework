package com.openaf.table.server.datasources

import com.openaf.table.lib.api._
import org.scalatest.FunSuite
import com.openaf.table.server.datasources.DataSourceTestData._

class UnfilteredArrayTableDataSourceTransformedTest extends FunSuite {
  test("1 row (transformed to Double), 0 measure, 0 column") {
    val ageField = AgeField.withTransformerType(IntToDoubleTransformerType)
    val tableState = TableState.Blank.withRowHeaderFields(List(ageField))

    val expectedRowHeaderValues = Set(List(1), List(2), List(3), List(4))
    val expectedValueLookUp = Map(AgeField.id -> List(AgeField.id, 36.0, 31.0, 34.0, 32.0))
    val expectedFieldValues = ageFieldValues(ageField.withKey(RowHeaderFieldKey(0)))

    val pivotData = check(tableState, expectedRowHeaderValues, Set.empty, Map.empty, expectedFieldValues, expectedValueLookUp)

    assert(pivotData.valueLookUp(AgeField.id).tail.forall(_.isInstanceOf[Double]), "Row field values should have been transformed to doubles")
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

  test("0 row, 1 measure (transformed to Double), 1 column") {
    val scoreField = ScoreField.withTransformerType(IntToDoubleTransformerType)
    val tableState = TableState.Blank.withColumnHeaderLayout(ColumnHeaderLayout(scoreField))

    val expectedColHeaders = Set(List(0,0))
    val expectedData = Map(p()(expectedColHeaders.head) -> 425.0)
    val expectedValueLookUp = Map(ScoreField.id -> List(ScoreField.id))
    val expectedFieldValues = emptyFieldValues(scoreField.withKey(ColumnHeaderFieldKey(0)))

    val pivotData = check(tableState, EmptyListSet, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)

    val aggregatorData = expectedData.map{case ((row,column),_) => (row,column) -> pivotData.aggregator(row.toArray,column.toArray)}
    assert(aggregatorData.values.forall(_.isInstanceOf[Double]), "All data should be transformed to doubles")
  }
}
