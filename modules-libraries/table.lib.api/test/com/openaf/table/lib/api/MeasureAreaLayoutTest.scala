package com.openaf.table.lib.api

import org.scalatest.FunSuite

class MeasureAreaLayoutTest extends FunSuite {
  val measureField1 = Field[Any]("measure1", Measure)
  val measureField2 = Field[Any]("measure2", Measure)
  val dimensionField1 = Field[Any]("dimensionField1")
  val dimensionField2 = Field[Any]("dimensionField2")
  val dimensionField3 = Field[Any]("dimensionField3")
  val layout1 = MeasureAreaLayout(measureField1, List(dimensionField1, dimensionField2))

  test("allFields") {
    assert(layout1.allFields === List(measureField1, dimensionField1, dimensionField2))
  }

  test("normalise 1") {
    val layoutToBeNormalised = MeasureAreaLayout(MeasureAreaTree(Right(MeasureAreaLayout.fromFields(dimensionField1, dimensionField2))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout.fromFields(dimensionField1, dimensionField2)
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
      MeasureAreaTree(Right(MeasureAreaLayout.fromFields(dimensionField1, dimensionField1)),
        MeasureAreaLayout.fromFields(dimensionField2)
      )
    )
    val normalisedLayout = layoutThatShouldStayTheSame.normalise
    assert(normalisedLayout === layoutThatShouldStayTheSame)
  }

  test("normalise 13") {
    val layoutToBeNormalised = MeasureAreaLayout(
      MeasureAreaTree(Right(MeasureAreaLayout(dimensionField1, List(dimensionField2))),
        MeasureAreaLayout(dimensionField3)
      )
    )
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(
      MeasureAreaTree(dimensionField1,
        MeasureAreaLayout(MeasureAreaTree(dimensionField2, MeasureAreaLayout(dimensionField3)))
      )
    )
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 14") {
    val layoutToBeNormalised = MeasureAreaLayout(List(
      MeasureAreaTree(Right(MeasureAreaLayout(List(
        MeasureAreaTree(Right(MeasureAreaLayout(List(
          MeasureAreaTree(Right(MeasureAreaLayout(List(
            MeasureAreaTree(Left(dimensionField1))))),
            MeasureAreaLayout(List(MeasureAreaTree(Left(dimensionField2))))
          )
        )))),
        MeasureAreaTree(Left(dimensionField3)))
      )))
    ))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = MeasureAreaLayout(List(
      MeasureAreaTree(dimensionField1, MeasureAreaLayout(dimensionField2)),
      MeasureAreaTree(dimensionField3)
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("paths") {
    val layout1 = MeasureAreaLayout(dimensionField1)
    val expected1 = List(MeasureAreaLayoutPath(List(dimensionField1)))
    assert(layout1.paths === expected1)

    val layout2 = MeasureAreaLayout(dimensionField1, List(dimensionField2, dimensionField3))
    val expected2 = List(
      MeasureAreaLayoutPath(List(dimensionField1, dimensionField2)),
      MeasureAreaLayoutPath(List(dimensionField1, dimensionField3))
    )
    assert(layout2.paths === expected2)

    val layout3 = MeasureAreaLayout(List(
      MeasureAreaTree(List(dimensionField1), List(dimensionField2)),
      MeasureAreaTree(List(dimensionField3), List(dimensionField1, dimensionField2))
    ))
    val expected3 = List(
      MeasureAreaLayoutPath(List(dimensionField1, dimensionField2)),
      MeasureAreaLayoutPath(List(dimensionField3, dimensionField1)),
      MeasureAreaLayoutPath(List(dimensionField3, dimensionField2))
    )
    assert(layout3.paths === expected3)

    val layout4 = MeasureAreaLayout(
      MeasureAreaTree(List(dimensionField1, dimensionField2), List(dimensionField3, measureField1))
    )
    val expected4 = List(
      MeasureAreaLayoutPath(List(dimensionField1, dimensionField3)),
      MeasureAreaLayoutPath(List(dimensionField1, measureField1)),
      MeasureAreaLayoutPath(List(dimensionField2, dimensionField3)),
      MeasureAreaLayoutPath(List(dimensionField2, measureField1))
    )
    assert(layout4.paths === expected4)

    val layout5 = MeasureAreaLayout.fromFields(measureField1, dimensionField1)
    val expected5 = List(
      MeasureAreaLayoutPath(List(measureField1)),
      MeasureAreaLayoutPath(List(dimensionField1))
    )
    assert(layout5.paths === expected5)
  }

  test("single field path") {
    val expectedPathList = List(MeasureAreaLayoutPath(List(measureField1)))

    val layout1 = MeasureAreaLayout(MeasureAreaTree(measureField1))
    assert(layout1.paths === expectedPathList)

    val layout2 = MeasureAreaLayout(measureField1)
    assert(layout2.paths === expectedPathList)

    val layout3 = MeasureAreaLayout(List(measureField1), Nil)
    assert(layout3.paths === expectedPathList)

    val layout4 = MeasureAreaLayout(List(MeasureAreaTree(measureField1)))
    assert(layout4.paths === expectedPathList)

    val layout5 = MeasureAreaLayout.fromFields(List(measureField1))
    assert(layout5.paths === expectedPathList)

    val layout6 = MeasureAreaLayout.fromFields(measureField1)
    assert(layout6.paths === expectedPathList)
  }

  test("remove") {
    val layout1 = MeasureAreaLayout(measureField1)
    val result1 = layout1.remove(measureField1)
    assert(result1 === MeasureAreaLayout.Blank)

    val layout2 = MeasureAreaLayout(measureField1, List(dimensionField1))
    val result2 = layout2.remove(measureField1)
    assert(result2 === MeasureAreaLayout(dimensionField1))

    val result3 = layout2.remove(dimensionField1)
    assert(result3 === MeasureAreaLayout(measureField1))

    val result4 = layout2.remove(dimensionField2)
    assert(result4 === layout2)

    val layout5 = MeasureAreaLayout(measureField1, List(dimensionField1, dimensionField2))
    val result5 = layout5.remove(measureField1)
    assert(result5 === MeasureAreaLayout(List(dimensionField1, dimensionField2), Nil))

    val result6 = layout5.remove(dimensionField1)
    assert(result6 === MeasureAreaLayout(measureField1, List(dimensionField2)))

    val result7 = layout5.remove(dimensionField2)
    assert(result7 === MeasureAreaLayout(measureField1, List(dimensionField1)))

    val layout8 = MeasureAreaLayout(List(dimensionField1, dimensionField2, measureField1), Nil)
    val result8 = layout8.remove(dimensionField2)
    assert(result8 === MeasureAreaLayout(List(dimensionField1, measureField1), Nil))
  }

  test("addFieldToRight") {
    val result1 = MeasureAreaLayout.Blank.addFieldToRight(measureField1)
    assert(result1 === MeasureAreaLayout(measureField1))

    val result2 = MeasureAreaLayout(measureField1, List(dimensionField1, dimensionField2)).addFieldToRight(measureField2)
    assert(result2 === MeasureAreaLayout(List(
      MeasureAreaTree(List(measureField1), List(dimensionField1, dimensionField2)),
      MeasureAreaTree(measureField2)
    )).normalise)
  }
}
