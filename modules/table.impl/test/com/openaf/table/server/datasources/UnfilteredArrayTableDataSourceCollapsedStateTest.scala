package com.openaf.table.server.datasources

import org.scalatest.FunSuite
import DataSourceTestData._
import com.openaf.table.lib.api._
import TableValues._

class UnfilteredArrayTableDataSourceCollapsedStateTest extends FunSuite {
  test("2 row (first row, first value collapsed), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllExpanded(Set(CollapsedStatePath(Array(F))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,TotalTopInt), List(2,1), List(2,2), List(2,3))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,TotalTopInt)(ch) -> 180,
      p(2,1          )(ch) -> 80,
      p(2,2          )(ch) -> 90,
      p(2,3          )(ch) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first row, second value collapsed), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllExpanded(Set(CollapsedStatePath(Array(M))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(2,1), List(2,2), List(1,TotalTopInt))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(2,1          )(ch) -> 50,
      p(2,2          )(ch) -> 130,
      p(1,TotalTopInt)(ch) -> 245
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, M, F),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first row, second value expanded), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllCollapsed(Set(CollapsedStatePath(Array(M))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(2,TotalTopInt), List(1,1), List(1,2), List(1,3))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(2,TotalTopInt)(ch) -> 180,
      p(1,1          )(ch) -> 80,
      p(1,2          )(ch) -> 90,
      p(1,3          )(ch) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, M, F),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first row, first value collapsed, top total), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true, collapsedState = AllExpanded(Set(CollapsedStatePath(Array(F))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,TotalTopInt), List(2,TotalTopInt), List(2,1), List(2,2), List(2,3))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,TotalTopInt)(ch) -> 180,
      p(2,TotalTopInt)(ch) -> 245,
      p(2,1          )(ch) -> 80,
      p(2,2          )(ch) -> 90,
      p(2,3          )(ch) -> 75
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first row, first value collapsed, top and bottom total), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true, bottom = true, collapsedState = AllExpanded(Set(CollapsedStatePath(Array(F))))))
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,TotalTopInt), List(2,TotalTopInt), List(2,1), List(2,2), List(2,3), List(2,TotalBottomInt))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,TotalTopInt   )(ch) -> 180,
      p(2,TotalTopInt   )(ch) -> 245,
      p(2,1             )(ch) -> 80,
      p(2,2             )(ch) -> 90,
      p(2,3             )(ch) -> 75,
      p(2,TotalBottomInt)(ch) -> 245
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  private def checkAllCollapsed(genderField:Field[_]) {
    val tableState = TableState.Blank
      .withRowHeaderFields(List(genderField, LocationField))
      .withColumnHeaderLayout(ColumnHeaderLayout(ScoreField))

    val expectedRowHeaderValues = Set(List(1,TotalTopInt), List(2,TotalTopInt))
    val ch = List(0,0)
    val expectedColHeaders = Set(ch)
    val expectedData = Map(
      p(1,TotalTopInt)(ch) -> 180,
      p(2,TotalTopInt)(ch) -> 245
    )
    val expectedValueLookUp = Map(
      GenderField.id -> List(GenderField.id, F, M),
      LocationField.id -> List(LocationField.id, London, Manchester, Edinburgh),
      ScoreField.id -> List(ScoreField.id)
    )
    val expectedFieldValues = genderFieldValues(genderField.withKey(RowHeaderFieldKey(0))) ++
      locationFieldValues(LocationField.withKey(RowHeaderFieldKey(1))) ++
      scoreFieldValues(ScoreField.withKey(ColumnHeaderFieldKey(0)))

    check(tableState, expectedRowHeaderValues, expectedColHeaders, expectedData, expectedFieldValues, expectedValueLookUp)
  }

  test("2 row (first row, all values collapsed), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(collapsedState = AllCollapsed()))
    checkAllCollapsed(genderField)
  }

  test("2 row (first row, all values collapsed, top and bottom totals), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true, bottom = true, collapsedState = AllCollapsed()))
    checkAllCollapsed(genderField)
  }

  test("2 row (first row, all values collapsed, top total), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(top = true, collapsedState = AllCollapsed()))
    checkAllCollapsed(genderField)
  }

  test("2 row (first row, all values collapsed, bottom total), 1 measure, 0 column") {
    val genderField = GenderField.withTotals(Totals(bottom = true, collapsedState = AllCollapsed()))
    checkAllCollapsed(genderField)
  }
}
