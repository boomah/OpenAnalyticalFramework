package com.openaf.table.api

import org.scalatest.FunSuite

class MeasureAreaLayoutTest extends FunSuite {
  val measureField1 = Field("measure1")
  val keyField1 = Field("keyField1")
  val keyField2 = Field("keyField2")
  val layout1 = MeasureAreaLayout(measureField1, List(keyField1, keyField2))

  test("allFields") {
    assert(layout1.allFields === Set(measureField1, keyField1, keyField2))
  }
}
