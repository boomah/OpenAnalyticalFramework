package com.openaf.table.api

import org.scalatest.FunSuite

class MeasureAreaLayoutTest extends FunSuite {
  val measureField1 = Field("measure1", Measure)
  val dimensionField1 = Field("dimensionField1")
  val dimensionField2 = Field("dimensionField2")
  val dimensionField3 = Field("dimensionField3")
  val layout1 = MeasureAreaLayout(measureField1, List(dimensionField1, dimensionField2))

  test("allFields") {
    assert(layout1.allFields === List(measureField1, dimensionField1, dimensionField2))
  }

  test("normalise 1") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(dimensionField1, dimensionField2)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout.fromFields(List(dimensionField1, dimensionField2))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 2") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Left(measureField1),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(dimensionField1))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1, List(dimensionField1))
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
      MeasureAreaLayout(MeasureAreaTree(Left(dimensionField1)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(dimensionField1)
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 5") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Left(measureField1),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)), MeasureAreaLayout(dimensionField1)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1, List(dimensionField1))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 6") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)),
      MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout(Nil)), MeasureAreaLayout(measureField1, List(dimensionField1, dimensionField2))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(measureField1, List(dimensionField1, dimensionField2))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 7") {
    val layoutToBeNormalised = MeasureAreaLayout(
      List(MeasureAreaTree(Right(MeasureAreaLayout(
        List(MeasureAreaTree(Right(MeasureAreaLayout(
          List(MeasureAreaTree(Left(dimensionField1)), MeasureAreaTree(Left(dimensionField2))))))))),
        MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField3)))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(dimensionField1, dimensionField2))), MeasureAreaLayout(dimensionField3)))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 8") {
    val layoutToBeNormalised = MeasureAreaLayout(
      MeasureAreaTree(
        Right(MeasureAreaLayout(List(
          MeasureAreaTree(dimensionField1), MeasureAreaTree(dimensionField2, MeasureAreaLayout(dimensionField3))
        )))
      )
    )
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(List(
      MeasureAreaTree(dimensionField1), MeasureAreaTree(dimensionField2, MeasureAreaLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 9") {
    val layoutToBeNormalised = MeasureAreaLayout(
      MeasureAreaTree(Right(
        MeasureAreaLayout(dimensionField1)
      ))
    )
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(List(MeasureAreaTree(dimensionField1)))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 10") {
    val layoutToBeNormalised = MeasureAreaLayout(List(
      MeasureAreaTree(Right(MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField1),MeasureAreaLayout(Nil))))),MeasureAreaLayout(Nil)),
      MeasureAreaTree(Left(dimensionField2),MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField3),MeasureAreaLayout(Nil)))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(List(
      MeasureAreaTree(dimensionField1), MeasureAreaTree(dimensionField2, MeasureAreaLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise doesn't change when called more than once") {
    val layoutToBeNormalised = MeasureAreaLayout(List(
      MeasureAreaTree(Right(MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField1),MeasureAreaLayout(Nil))))),MeasureAreaLayout(Nil)),
      MeasureAreaTree(Left(dimensionField2),MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField3),MeasureAreaLayout(Nil)))))))
    val normalisedLayout = layoutToBeNormalised.normalise.normalise
    val expectedLayout = MeasureAreaLayout(List(
      MeasureAreaTree(dimensionField1), MeasureAreaTree(dimensionField2, MeasureAreaLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 11") {
    val layoutToBeNormalised = MeasureAreaLayout(List(
      MeasureAreaTree(Right(
        MeasureAreaLayout(List(
          MeasureAreaTree(Right(
            MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField1),MeasureAreaLayout(Nil))))
          ), MeasureAreaLayout(Nil)),
          MeasureAreaTree(Left(dimensionField2),
            MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField3),MeasureAreaLayout(Nil)))))))),
        MeasureAreaLayout(Nil))
    ))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(List(
      MeasureAreaTree(dimensionField1), MeasureAreaTree(dimensionField2, MeasureAreaLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 12") {
    val layoutToBeNormalised = MeasureAreaLayout(List(
      MeasureAreaTree(Right(
        MeasureAreaLayout(List(
          MeasureAreaTree(Right(
            MeasureAreaLayout(List(
              MeasureAreaTree(Right(
                MeasureAreaLayout(List(
                  MeasureAreaTree(Left(dimensionField1),MeasureAreaLayout(List()))
                ))
              ), MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField2),MeasureAreaLayout(List())))))
            ))
          ),MeasureAreaLayout(List()))
        ))
      ),MeasureAreaLayout(List()))
    ))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(List(
      MeasureAreaTree(dimensionField1, MeasureAreaLayout(dimensionField2))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise doesn't remove fields it shouldn't") {
    val layoutThatShouldStayTheSame = MeasureAreaLayout(
      MeasureAreaTree(Right(MeasureAreaLayout.fromFields(List(dimensionField1, dimensionField1))),
        MeasureAreaLayout.fromFields(List(dimensionField2))
      )
    )
    val normalisedLayout = layoutThatShouldStayTheSame.normalise
    assert(normalisedLayout === layoutThatShouldStayTheSame)
  }
}
