package com.openaf.table.lib.api

import org.scalatest.FunSuite

class ColumnHeaderLayoutTest extends FunSuite {
  val measureField1 = Field[Any]("measure1", Measure)
  val measureField2 = Field[Any]("measure2", Measure)
  val dimensionField1 = Field[Any]("dimensionField1")
  val dimensionField2 = Field[Any]("dimensionField2")
  val dimensionField3 = Field[Any]("dimensionField3")
  val layout1 = ColumnHeaderLayout(measureField1, List(dimensionField1, dimensionField2))

  test("allFields") {
    assert(layout1.allFields === List(measureField1, dimensionField1, dimensionField2))
  }

  test("normalise 1") {
    val layoutToBeNormalised = ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout.fromFields(dimensionField1, dimensionField2))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout.fromFields(dimensionField1, dimensionField2)
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 2") {
    val layoutToBeNormalised = ColumnHeaderLayout(ColumnHeaderTree(Left(measureField1),
      ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout(dimensionField1))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(measureField1, List(dimensionField1))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 3") {
    val layoutToBeNormalised = ColumnHeaderLayout(ColumnHeaderTree(Left(measureField1),
      ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout(Nil))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(measureField1)
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 4") {
    val layoutToBeNormalised = ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout(Nil)),
      ColumnHeaderLayout(ColumnHeaderTree(Left(dimensionField1)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(dimensionField1)
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 5") {
    val layoutToBeNormalised = ColumnHeaderLayout(ColumnHeaderTree(Left(measureField1),
      ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout(Nil)), ColumnHeaderLayout(dimensionField1)))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(measureField1, List(dimensionField1))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 6") {
    val layoutToBeNormalised = ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout(Nil)),
      ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout(Nil)), ColumnHeaderLayout(measureField1, List(dimensionField1, dimensionField2))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(measureField1, List(dimensionField1, dimensionField2))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 7") {
    val layoutToBeNormalised = ColumnHeaderLayout(
      List(ColumnHeaderTree(Right(ColumnHeaderLayout(
        List(ColumnHeaderTree(Right(ColumnHeaderLayout(
          List(ColumnHeaderTree(Left(dimensionField1)), ColumnHeaderTree(Left(dimensionField2))))))))),
        ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField3)))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(ColumnHeaderTree(Right(ColumnHeaderLayout.fromFields(List(dimensionField1, dimensionField2))), ColumnHeaderLayout(dimensionField3)))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 8") {
    val layoutToBeNormalised = ColumnHeaderLayout(
      ColumnHeaderTree(
        Right(ColumnHeaderLayout(List(
          ColumnHeaderTree(dimensionField1), ColumnHeaderTree(dimensionField2, ColumnHeaderLayout(dimensionField3))
        )))
      )
    )
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(dimensionField1), ColumnHeaderTree(dimensionField2, ColumnHeaderLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 9") {
    val layoutToBeNormalised = ColumnHeaderLayout(
      ColumnHeaderTree(Right(
        ColumnHeaderLayout(dimensionField1)
      ))
    )
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(List(ColumnHeaderTree(dimensionField1)))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 10") {
    val layoutToBeNormalised = ColumnHeaderLayout(List(
      ColumnHeaderTree(Right(ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField1),ColumnHeaderLayout(Nil))))),ColumnHeaderLayout(Nil)),
      ColumnHeaderTree(Left(dimensionField2),ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField3),ColumnHeaderLayout(Nil)))))))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(dimensionField1), ColumnHeaderTree(dimensionField2, ColumnHeaderLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise doesn't change when called more than once") {
    val layoutToBeNormalised = ColumnHeaderLayout(List(
      ColumnHeaderTree(Right(ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField1),ColumnHeaderLayout(Nil))))),ColumnHeaderLayout(Nil)),
      ColumnHeaderTree(Left(dimensionField2),ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField3),ColumnHeaderLayout(Nil)))))))
    val normalisedLayout = layoutToBeNormalised.normalise.normalise
    val expectedLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(dimensionField1), ColumnHeaderTree(dimensionField2, ColumnHeaderLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 11") {
    val layoutToBeNormalised = ColumnHeaderLayout(List(
      ColumnHeaderTree(Right(
        ColumnHeaderLayout(List(
          ColumnHeaderTree(Right(
            ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField1),ColumnHeaderLayout(Nil))))
          ), ColumnHeaderLayout(Nil)),
          ColumnHeaderTree(Left(dimensionField2),
            ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField3),ColumnHeaderLayout(Nil)))))))),
        ColumnHeaderLayout(Nil))
    ))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(dimensionField1), ColumnHeaderTree(dimensionField2, ColumnHeaderLayout(dimensionField3))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 12") {
    val layoutToBeNormalised = ColumnHeaderLayout(List(
      ColumnHeaderTree(Right(
        ColumnHeaderLayout(List(
          ColumnHeaderTree(Right(
            ColumnHeaderLayout(List(
              ColumnHeaderTree(Right(
                ColumnHeaderLayout(List(
                  ColumnHeaderTree(Left(dimensionField1),ColumnHeaderLayout(List()))
                ))
              ), ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField2),ColumnHeaderLayout(List())))))
            ))
          ),ColumnHeaderLayout(List()))
        ))
      ),ColumnHeaderLayout(List()))
    ))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(dimensionField1, ColumnHeaderLayout(dimensionField2))
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise doesn't remove fields it shouldn't") {
    val layoutThatShouldStayTheSame = ColumnHeaderLayout(
      ColumnHeaderTree(Right(ColumnHeaderLayout.fromFields(dimensionField1, dimensionField1)),
        ColumnHeaderLayout.fromFields(dimensionField2)
      )
    )
    val normalisedLayout = layoutThatShouldStayTheSame.normalise
    assert(normalisedLayout === layoutThatShouldStayTheSame)
  }

  test("normalise 13") {
    val layoutToBeNormalised = ColumnHeaderLayout(
      ColumnHeaderTree(Right(ColumnHeaderLayout(dimensionField1, List(dimensionField2))),
        ColumnHeaderLayout(dimensionField3)
      )
    )
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(
      ColumnHeaderTree(dimensionField1,
        ColumnHeaderLayout(ColumnHeaderTree(dimensionField2, ColumnHeaderLayout(dimensionField3)))
      )
    )
    assert(normalisedLayout === expectedLayout)
  }

  test("normalise 14") {
    val layoutToBeNormalised = ColumnHeaderLayout(List(
      ColumnHeaderTree(Right(ColumnHeaderLayout(List(
        ColumnHeaderTree(Right(ColumnHeaderLayout(List(
          ColumnHeaderTree(Right(ColumnHeaderLayout(List(
            ColumnHeaderTree(Left(dimensionField1))))),
            ColumnHeaderLayout(List(ColumnHeaderTree(Left(dimensionField2))))
          )
        )))),
        ColumnHeaderTree(Left(dimensionField3)))
      )))
    ))
    val normalisedLayout = layoutToBeNormalised.normalise
    val expectedLayout = ColumnHeaderLayout(List(
      ColumnHeaderTree(dimensionField1, ColumnHeaderLayout(dimensionField2)),
      ColumnHeaderTree(dimensionField3)
    ))
    assert(normalisedLayout === expectedLayout)
  }

  test("paths") {
    val layout1 = ColumnHeaderLayout(dimensionField1)
    val expected1 = List(ColumnHeaderLayoutPath(List(dimensionField1)))
    assert(layout1.paths === expected1)

    val layout2 = ColumnHeaderLayout(dimensionField1, List(dimensionField2, dimensionField3))
    val expected2 = List(
      ColumnHeaderLayoutPath(List(dimensionField1, dimensionField2)),
      ColumnHeaderLayoutPath(List(dimensionField1, dimensionField3))
    )
    assert(layout2.paths === expected2)

    val layout3 = ColumnHeaderLayout(List(
      ColumnHeaderTree(List(dimensionField1), List(dimensionField2)),
      ColumnHeaderTree(List(dimensionField3), List(dimensionField1, dimensionField2))
    ))
    val expected3 = List(
      ColumnHeaderLayoutPath(List(dimensionField1, dimensionField2)),
      ColumnHeaderLayoutPath(List(dimensionField3, dimensionField1)),
      ColumnHeaderLayoutPath(List(dimensionField3, dimensionField2))
    )
    assert(layout3.paths === expected3)

    val layout4 = ColumnHeaderLayout(
      ColumnHeaderTree(List(dimensionField1, dimensionField2), List(dimensionField3, measureField1))
    )
    val expected4 = List(
      ColumnHeaderLayoutPath(List(dimensionField1, dimensionField3)),
      ColumnHeaderLayoutPath(List(dimensionField1, measureField1)),
      ColumnHeaderLayoutPath(List(dimensionField2, dimensionField3)),
      ColumnHeaderLayoutPath(List(dimensionField2, measureField1))
    )
    assert(layout4.paths === expected4)

    val layout5 = ColumnHeaderLayout.fromFields(measureField1, dimensionField1)
    val expected5 = List(
      ColumnHeaderLayoutPath(List(measureField1)),
      ColumnHeaderLayoutPath(List(dimensionField1))
    )
    assert(layout5.paths === expected5)
  }

  test("single field path") {
    val expectedPathList = List(ColumnHeaderLayoutPath(List(measureField1)))

    val layout1 = ColumnHeaderLayout(ColumnHeaderTree(measureField1))
    assert(layout1.paths === expectedPathList)

    val layout2 = ColumnHeaderLayout(measureField1)
    assert(layout2.paths === expectedPathList)

    val layout3 = ColumnHeaderLayout(List(measureField1), Nil)
    assert(layout3.paths === expectedPathList)

    val layout4 = ColumnHeaderLayout(List(ColumnHeaderTree(measureField1)))
    assert(layout4.paths === expectedPathList)

    val layout5 = ColumnHeaderLayout.fromFields(List(measureField1))
    assert(layout5.paths === expectedPathList)

    val layout6 = ColumnHeaderLayout.fromFields(measureField1)
    assert(layout6.paths === expectedPathList)
  }

  test("remove") {
    val layout1 = ColumnHeaderLayout(measureField1)
    val result1 = layout1.remove(measureField1)
    assert(result1 === ColumnHeaderLayout.Blank)

    val layout2 = ColumnHeaderLayout(measureField1, List(dimensionField1))
    val result2 = layout2.remove(measureField1)
    assert(result2 === ColumnHeaderLayout(dimensionField1))

    val result3 = layout2.remove(dimensionField1)
    assert(result3 === ColumnHeaderLayout(measureField1))

    val result4 = layout2.remove(dimensionField2)
    assert(result4 === layout2)

    val layout5 = ColumnHeaderLayout(measureField1, List(dimensionField1, dimensionField2))
    val result5 = layout5.remove(measureField1)
    assert(result5 === ColumnHeaderLayout(List(dimensionField1, dimensionField2), Nil))

    val result6 = layout5.remove(dimensionField1)
    assert(result6 === ColumnHeaderLayout(measureField1, List(dimensionField2)))

    val result7 = layout5.remove(dimensionField2)
    assert(result7 === ColumnHeaderLayout(measureField1, List(dimensionField1)))

    val layout8 = ColumnHeaderLayout(List(dimensionField1, dimensionField2, measureField1), Nil)
    val result8 = layout8.remove(dimensionField2)
    assert(result8 === ColumnHeaderLayout(List(dimensionField1, measureField1), Nil))
  }

  test("addFieldToRight") {
    val result1 = ColumnHeaderLayout.Blank.addFieldToRight(measureField1)
    assert(result1 === ColumnHeaderLayout(measureField1))

    val result2 = ColumnHeaderLayout(measureField1, List(dimensionField1, dimensionField2)).addFieldToRight(measureField2)
    assert(result2 === ColumnHeaderLayout(List(
      ColumnHeaderTree(List(measureField1), List(dimensionField1, dimensionField2)),
      ColumnHeaderTree(measureField2)
    )).normalise)
  }
}
