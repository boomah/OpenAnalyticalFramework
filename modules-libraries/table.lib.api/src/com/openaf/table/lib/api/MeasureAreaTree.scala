package com.openaf.table.lib.api

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
    measureAreaTreeType match {
      case field@Left(_) => List(MeasureAreaTree(field, normalisedChildMeasureAreaLayout))
      case Right(measureAreaLayout) => {
        val normalisedMeasureAreaLayout = measureAreaLayout.normalise
        val allFields = normalisedMeasureAreaLayout.allFields
        if (allFields.isEmpty) {
          normalisedChildMeasureAreaLayout.measureAreaTrees
        } else if (allFields.size == 1) {
          List(MeasureAreaTree(Left(allFields.head), normalisedChildMeasureAreaLayout))
        } else {
          normalisedMeasureAreaLayout.measureAreaTrees match {
            case measureAreaTree :: Nil if measureAreaTree.measureAreaTreeType.isLeft && measureAreaTree.childMeasureAreaLayout.allFields.nonEmpty => {
              val newChildMeasureAreaTree = MeasureAreaTree(Right(measureAreaTree.childMeasureAreaLayout), normalisedChildMeasureAreaLayout)
              val newChildMeasureAreaLayout = MeasureAreaLayout(newChildMeasureAreaTree).normalise
              List(MeasureAreaTree(measureAreaTree.measureAreaTreeType, newChildMeasureAreaLayout))
            }
            case _ => List(MeasureAreaTree(Right(normalisedMeasureAreaLayout), normalisedChildMeasureAreaLayout))
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