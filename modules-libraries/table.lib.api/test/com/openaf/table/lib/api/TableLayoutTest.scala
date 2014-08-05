package com.openaf.table.lib.api

import org.scalatest.FunSuite

class TableLayoutTest extends FunSuite {
  val measureField1 = Field[Any]("measure1", Measure)
  val measureField2 = Field[Any]("measure2", Measure)
  val dimensionField1 = Field[Any]("dimensionField1")
  val dimensionField1Duplicate = dimensionField1.duplicate
  val dimensionField2 = Field[Any]("dimensionField2")
  val dimensionField3 = Field[Any]("dimensionField3")

  test("no duplicate fields") {
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField1))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withFilterFields(List(dimensionField1, dimensionField1))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1, measureField1))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withFilterFields(List(dimensionField1))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withFilterFields(List(dimensionField2))
        .withColumnHeaderLayout(ColumnHeaderLayout.fromFields(dimensionField1))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withFilterFields(List(dimensionField2))
        .withColumnHeaderLayout(ColumnHeaderLayout.fromFields(dimensionField2))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withFilterFields(List(dimensionField2))
        .withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1, dimensionField1))
    }
    intercept[IllegalArgumentException] {
      TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout.fromFields(dimensionField1, dimensionField1.flipSortOrder))
    }
  }

  test("remove") {
    val layout1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1))
    assert(layout1.remove(dimensionField1) === TableLayout.Blank)
    assert(layout1.remove(dimensionField2) === layout1)

    val layout2 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField2, dimensionField3))
    assert(layout2.remove(dimensionField2) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField3)))
    assert(layout2.remove(List(dimensionField2, dimensionField3)) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1)))
    assert(layout2.remove(List(dimensionField1, dimensionField3)) === TableLayout.Blank.withRowHeaderFields(List(dimensionField2)))

    val layout3 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField1Duplicate))
    assert(layout3.remove(dimensionField1Duplicate) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1)))

    val layout4 = TableLayout.Blank.withFilterFields(List(dimensionField1, dimensionField2, dimensionField3))
    assert(layout4.remove(dimensionField2) === TableLayout.Blank.withFilterFields(List(dimensionField1, dimensionField3)))
    assert(layout4.remove(List(dimensionField2, dimensionField3)) === TableLayout.Blank.withFilterFields(List(dimensionField1)))
    assert(layout4.remove(List(dimensionField1, dimensionField3)) === TableLayout.Blank.withFilterFields(List(dimensionField2)))

    val layout5 = TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1, measureField2))
    assert(layout5.remove(measureField1) === TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField2)))

    val layout6 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withFilterFields(List(dimensionField2))
      .withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1))
    assert(layout6.remove(dimensionField1) === TableLayout.Blank.withFilterFields(List(dimensionField2)).withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1)))
    assert(layout6.remove(dimensionField2) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1)))
    assert(layout6.remove(measureField1) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1)).withFilterFields(List(dimensionField2)))
    assert(layout6.remove(List(dimensionField1, dimensionField2)) === TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout.fromFields(measureField1)))
    assert(layout6.remove(List(dimensionField1, measureField1)) === TableLayout.Blank.withFilterFields(List(dimensionField2)))
    assert(layout6.remove(List(dimensionField1, dimensionField2, measureField1)) === TableLayout.Blank)
  }
}
