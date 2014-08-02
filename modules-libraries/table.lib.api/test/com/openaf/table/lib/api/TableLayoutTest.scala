package com.openaf.table.lib.api

import org.scalatest.FunSuite

class TableLayoutTest extends FunSuite {
  val measureField1 = Field[Any]("measure1", Measure)
  val measureField2 = Field[Any]("measure2", Measure)
  val dimensionField1 = Field[Any]("dimensionField1")
  val dimensionField2 = Field[Any]("dimensionField2")
  val dimensionField3 = Field[Any]("dimensionField3")

  test("remove") {
    val layout1 = TableLayout.Blank.withRowHeaderFields(List(dimensionField1))
    assert(layout1.remove(dimensionField1) === TableLayout.Blank)
    assert(layout1.remove(dimensionField2) === layout1)
  }
}
