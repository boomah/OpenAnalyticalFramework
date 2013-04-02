package com.openaf.table.api

import org.scalatest.FunSuite

class MeasureAreaLayoutTest extends FunSuite {
  val measureField1 = Field("measure1", Measure)
  val splitField1 = Field("splitField1")
  val splitField2 = Field("splitField2")
  val splitField3 = Field("splitField3")
  val layout1 = MeasureAreaLayout(measureField1, List(splitField1, splitField2))

  test("allFields") {
    assert(layout1.allFields === Set(measureField1, splitField1, splitField2))
  }

  test("normalise 1") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(splitField1, splitField2)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout.fromFields(List(splitField1, splitField2))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 2") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Left(measureField1),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(splitField1))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1, List(splitField1))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 3") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Left(measureField1),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1)
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 4") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)),
      MeasureAreaLayout(MeasureAreaTree(Left(splitField1)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(splitField1)
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 5") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Left(measureField1),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)), MeasureAreaLayout(splitField1)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1, List(splitField1))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 6") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)), MeasureAreaLayout(measureField1, List(splitField1, splitField2))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1, List(splitField1, splitField2))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 7") {
    val layoutToBeNormalised = MeasureAreaLayout(
      List(MeasureAreaTree(Right(MeasureAreaLayout(
        List(MeasureAreaTree(Right(MeasureAreaLayout(
          List(MeasureAreaTree(Left(splitField1)), MeasureAreaTree(Left(splitField2))))))))),
        MeasureAreaLayout(List(MeasureAreaTree(Left(splitField3)))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(splitField1, splitField2))), MeasureAreaLayout(splitField3)))
    assert(normalisedLayout === expectedLayout)
  }
}
