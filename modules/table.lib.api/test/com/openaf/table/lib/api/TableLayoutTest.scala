package com.openaf.table.lib.api

import org.scalatest.FunSuite

class TableLayoutTest extends FunSuite {
  val measureField1 = Field[Any]("measure1", Measure)
  val measureField2 = Field[Any]("measure2", Measure)
  val dimensionField1 = Field[Any]("dimensionField1")
  val dimensionField1With0thRowKey = dimensionField1.withKey(RowHeaderFieldKey(0))
  val dimensionField1With1stRowKey = dimensionField1.withKey(RowHeaderFieldKey(1))
  val dimensionField2 = Field[Any]("dimensionField2")
  val dimensionField3 = Field[Any]("dimensionField3")

  test("remove") {
    val layout1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1))
    assert(layout1.remove(dimensionField1) === TableLayout.Blank)
    assert(layout1.remove(dimensionField2) === layout1)

    val layout2 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField2, dimensionField3))
    assert(layout2.remove(dimensionField2) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField3)))
    assert(layout2.remove(List(dimensionField2, dimensionField3)) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1)))
    assert(layout2.remove(List(dimensionField1, dimensionField3)) === TableLayout.Blank.withRowHeaderFields(List(dimensionField2)))

    val layout3 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField1))
    intercept[IllegalArgumentException] {
      layout3.remove(dimensionField1)
    }
    assert(layout3.generateFieldKeys.remove(dimensionField1With0thRowKey) === TableLayout.Blank.withRowHeaderFields(List(dimensionField1With1stRowKey)))

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

  test("replaceField") {
    val layout1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1))
    val expectedResult1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField2))
    assert(layout1.replaceField(dimensionField1, dimensionField2) === expectedResult1)

    val layout2 = TableLayout.Blank.withFilterFields(List(dimensionField1))
    val expectedResult2 = TableLayout.Blank.withFilterFields(List(dimensionField2))
    assert(layout2.replaceField(dimensionField1, dimensionField2) === expectedResult2)

    val layout3 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField2))
    val expectedResult3 = TableLayout.Blank.withRowHeaderFields(List(dimensionField3, dimensionField2))
    assert(layout3.replaceField(dimensionField1, dimensionField3) === expectedResult3)

    val layout4 = TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout(measureField1))
    val expectedLayout4 = TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout(measureField2))
    assert(layout4.replaceField(measureField1, measureField2) === expectedLayout4)

    // TODO - do more complicated column layouts
  }

  test("generateFieldKeys") {
    val layout1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1))
    val expectedLayout1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1.withKey(RowHeaderFieldKey(0))))
    assert(layout1.generateFieldKeys === expectedLayout1)

    val layout2 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField1))
    val expectedLayout2 = TableLayout.Blank.withRowHeaderFields(
      List(dimensionField1.withKey(RowHeaderFieldKey(0)), dimensionField1.withKey(RowHeaderFieldKey(1)))
    )
    assert(layout2.generateFieldKeys === expectedLayout2)

    val layout3 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1, dimensionField2))
    val expectedLayout3 = TableLayout.Blank.withRowHeaderFields(
      List(dimensionField1.withKey(RowHeaderFieldKey(0)), dimensionField2.withKey(RowHeaderFieldKey(1)))
    )
    assert(layout3.generateFieldKeys === expectedLayout3)

    val layout4 = TableLayout.Blank.withFilterFields(List(dimensionField1))
    val expectedLayout4 = TableLayout.Blank.withFilterFields(List(dimensionField1.withKey(FilterFieldKey(0))))
    assert(layout4.generateFieldKeys === expectedLayout4)

    val layout5 = TableLayout.Blank.withFilterFields(List(dimensionField1, dimensionField1))
    val expectedLayout5 = TableLayout.Blank.withFilterFields(
      List(dimensionField1.withKey(FilterFieldKey(0)), dimensionField1.withKey(FilterFieldKey(1)))
    )
    assert(layout5.generateFieldKeys === expectedLayout5)

    val layout6 = TableLayout.Blank.withFilterFields(List(dimensionField1, dimensionField2))
    val expectedLayout6 = TableLayout.Blank.withFilterFields(
      List(dimensionField1.withKey(FilterFieldKey(0)), dimensionField2.withKey(FilterFieldKey(1)))
    )
    assert(layout6.generateFieldKeys === expectedLayout6)

    val layout7 = TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout(measureField1))
    val expectedLayout7 = TableLayout.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(measureField1.withKey(ColumnHeaderFieldKey(0)))
    )
    assert(layout7.generateFieldKeys === expectedLayout7)

    val layout8 = TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout(measureField1, List(measureField1)))
    val expectedLayout8 = TableLayout.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(measureField1.withKey(ColumnHeaderFieldKey(0)),
        List(measureField1.withKey(ColumnHeaderFieldKey(1))))
    )
    assert(layout8.generateFieldKeys === expectedLayout8)

    val layout9 = TableLayout.Blank.withColumnHeaderLayout(ColumnHeaderLayout(measureField1, List(measureField2)))
    val expectedLayout9 = TableLayout.Blank.withColumnHeaderLayout(
      ColumnHeaderLayout(measureField1.withKey(ColumnHeaderFieldKey(0)),
        List(measureField2.withKey(ColumnHeaderFieldKey(1))))
    )
    assert(layout9.generateFieldKeys === expectedLayout9)

    val layout10 = TableLayout.Blank
      .withRowHeaderFields(List(dimensionField1))
      .withColumnHeaderLayout(ColumnHeaderLayout(measureField1, List(dimensionField1)))
      .withFilterFields(List(dimensionField1))
    val expectedLayout10 = TableLayout.Blank
      .withRowHeaderFields(List(dimensionField1.withKey(RowHeaderFieldKey(0))))
      .withColumnHeaderLayout(ColumnHeaderLayout(measureField1.withKey(ColumnHeaderFieldKey(0)),
                                List(dimensionField1.withKey(ColumnHeaderFieldKey(1)))))
      .withFilterFields(List(dimensionField1.withKey(FilterFieldKey(0))))

    assert(layout10.generateFieldKeys === expectedLayout10)

    val layout11 = TableLayout.Blank
      .withRowHeaderFields(List(dimensionField1))
      .withColumnHeaderLayout(ColumnHeaderLayout(measureField1, List(dimensionField2)))
      .withFilterFields(List(dimensionField3))
    val expectedLayout11 = TableLayout.Blank
      .withRowHeaderFields(List(dimensionField1.withKey(RowHeaderFieldKey(0))))
      .withColumnHeaderLayout(ColumnHeaderLayout(measureField1.withKey(ColumnHeaderFieldKey(0)),
                                List(dimensionField2.withKey(ColumnHeaderFieldKey(1)))))
      .withFilterFields(List(dimensionField3.withKey(FilterFieldKey(0))))

    assert(layout11.generateFieldKeys === expectedLayout11)

    val columnHeaderLayout12 = ColumnHeaderLayout(List(measureField1, measureField2), List(dimensionField1, dimensionField2))
    val layout12 = TableLayout.Blank.withColumnHeaderLayout(columnHeaderLayout12)
    val expectedColumnHeaderLayout12 = ColumnHeaderLayout(
      List(measureField1.withKey(ColumnHeaderFieldKey(0)), measureField2.withKey(ColumnHeaderFieldKey(1))),
      List(dimensionField1.withKey(ColumnHeaderFieldKey(2)), dimensionField2.withKey(ColumnHeaderFieldKey(3)))
    )
    val expectedLayout12 = TableLayout.Blank.withColumnHeaderLayout(expectedColumnHeaderLayout12)
    assert(layout12.generateFieldKeys === expectedLayout12)
  }
}
