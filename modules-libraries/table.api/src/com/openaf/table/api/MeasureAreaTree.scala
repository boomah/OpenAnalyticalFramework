package com.openaf.table.api

import MeasureAreaLayout._

case class MeasureAreaTree(measureAreaTreeType:MeasureAreaTreeType, childMeasureAreaLayout:MeasureAreaLayout=MeasureAreaLayout.Blank) {
  def allFields:List[Field] = {
    (measureAreaTreeType match {
      case Left(field) => List(field)
      case Right(measureAreaLayout) => measureAreaLayout.allFields
    }) ++ childMeasureAreaLayout.allFields
  }
  def hasChildren = childMeasureAreaLayout.measureAreaTrees.nonEmpty

  def normalise:List[MeasureAreaTree] = {
    val normalisedChildMeasureAreaLayout = childMeasureAreaLayout.normalise
    if (normalisedChildMeasureAreaLayout.allFields.isEmpty) {
      measureAreaTreeType match {
        case field@Left(_) => List(MeasureAreaTree(field))
        case right@Right(measureAreaLayout) => {
          val normalisedMeasureAreaLayout = measureAreaLayout.normalise
          if (normalisedMeasureAreaLayout.measureAreaTrees.forall(_.allFields.size == 1)) {
            normalisedMeasureAreaLayout.measureAreaTrees.flatMap(_.allFields).map(field => MeasureAreaTree(field))
          } else {
            List(MeasureAreaTree(right))
          }
        }
      }
    } else {
      measureAreaTreeType match {
        case field@Left(_) => List(MeasureAreaTree(field, normalisedChildMeasureAreaLayout))
        case Right(measureAreaLayout) => {
          val normalisedMeasureAreaLayout = measureAreaLayout.normalise
          val allFields = normalisedMeasureAreaLayout.allFields
          if (allFields.isEmpty) {
            normalisedChildMeasureAreaLayout.measureAreaTrees
          } else if (allFields.size == 1) {
            List(MeasureAreaTree(Left(measureAreaLayout.allFields.head), normalisedChildMeasureAreaLayout))
          } else {
            normalisedMeasureAreaLayout.measureAreaTrees match {
              case measureAreaTree :: Nil if measureAreaTree.measureAreaTreeType.isLeft && measureAreaTree.childMeasureAreaLayout.allFields.nonEmpty => {
                val newChildMeasureAreaTree = MeasureAreaTree(Right(measureAreaTree.childMeasureAreaLayout), normalisedChildMeasureAreaLayout).normalise
                val newChildMeasureAreaLayout = MeasureAreaLayout(newChildMeasureAreaTree)
                List(MeasureAreaTree(measureAreaTree.measureAreaTreeType, newChildMeasureAreaLayout))
              }
              case _ => List(MeasureAreaTree(Right(normalisedMeasureAreaLayout), normalisedChildMeasureAreaLayout))
            }
          }
        }
      }
    }
  }
}

object MeasureAreaTree {
  def apply(field:Field):MeasureAreaTree = MeasureAreaTree(Left(field))
  def apply(field:Field, childMeasureAreaLayout:MeasureAreaLayout):MeasureAreaTree = {
    MeasureAreaTree(Left(field), childMeasureAreaLayout)
  }
  def apply(fields:Field*):MeasureAreaTree = {
    if (fields.size == 1) {
      MeasureAreaTree(fields.head)
    } else {
      val measureAreaLayout = MeasureAreaLayout.fromFields(fields.toList)
      MeasureAreaTree(Right(measureAreaLayout))
    }
    val measureAreaLayout = MeasureAreaLayout.fromFields(fields.toList)
    MeasureAreaTree(Right(measureAreaLayout))
  }
  def apply(topFields:List[Field], childFields:List[Field]):MeasureAreaTree = {
    MeasureAreaTree(Right(MeasureAreaLayout.fromFields(topFields)), MeasureAreaLayout.fromFields(childFields))
  }
}