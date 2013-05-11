package com.openaf.table.api

import MeasureAreaLayout._

case class MeasureAreaTree(measureAreaTreeType:MeasureAreaTreeType, childMeasureAreaLayout:MeasureAreaLayout=MeasureAreaLayout.Blank) {
  def allFields:Set[Field] = {
    (measureAreaTreeType match {
      case Left(field) => Set(field)
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
      val newMeasureAreaTreeTypeOption = measureAreaTreeType match {
        case field@Left(_) => Some(field)
        case right@Right(measureAreaLayout) => {
          val normalisedMeasureAreaLayout = measureAreaLayout.normalise
          val allFields = normalisedMeasureAreaLayout.allFields
          if (allFields.isEmpty) {
            None
          } else if (allFields.size == 1) {
            Some(Left(measureAreaLayout.allFields.head))
          } else {
            Some(Right(normalisedMeasureAreaLayout))
          }
        }
      }
      newMeasureAreaTreeTypeOption match {
        case Some(newMeasureAreaTreeType) => {
          List(MeasureAreaTree(newMeasureAreaTreeType, normalisedChildMeasureAreaLayout))
        }
        case _ => normalisedChildMeasureAreaLayout.measureAreaTrees
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
}