package com.openaf.table.lib.api

import MeasureAreaLayout._

case class MeasureAreaTree(measureAreaTreeType:MeasureAreaTreeType, childMeasureAreaLayout:MeasureAreaLayout=MeasureAreaLayout.Blank) {
  def allFields:List[Field[_]] = {
    (measureAreaTreeType match {
      case Left(field) => List(field)
      case Right(measureAreaLayout) => measureAreaLayout.allFields
    }) ++ childMeasureAreaLayout.allFields
  }
  def paths:List[MeasureAreaLayoutPath] = {
    measureAreaTreeType match {
      case Left(field) if childMeasureAreaLayout.measureAreaTrees.isEmpty => List(MeasureAreaLayoutPath(List(field)))
      case Left(field) => childMeasureAreaLayout.paths.map(path => MeasureAreaLayoutPath(field :: path.fields))
      case Right(measureAreaLayout) => {
        val paths = measureAreaLayout.paths
        val childPaths = childMeasureAreaLayout.paths
        paths.flatMap(path => childPaths.map(childPath => MeasureAreaLayoutPath(path.fields ::: childPath.fields)))
      }
    }
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

  def remove(fields:Field[_]*) = {
    val newMeasureAreaTreeType = measureAreaTreeType match {
      case Left(field) if fields.contains(field) => Right(MeasureAreaLayout.Blank)
      case field@Left(_) => field
      case Right(measureAreaLayout) => Right(measureAreaLayout.remove(fields:_*))
    }
    val newChildMeasureAreaLayout = childMeasureAreaLayout.remove(fields:_*)
    copy(measureAreaTreeType = newMeasureAreaTreeType, childMeasureAreaLayout = newChildMeasureAreaLayout)
  }
}

object MeasureAreaTree {
  def apply(field:Field[_]):MeasureAreaTree = MeasureAreaTree(Left(field))
  def apply(field:Field[_], childMeasureAreaLayout:MeasureAreaLayout):MeasureAreaTree = {
    MeasureAreaTree(Left(field), childMeasureAreaLayout)
  }
  def apply(fields:Field[_]*):MeasureAreaTree = {
    if (fields.size == 1) {
      MeasureAreaTree(fields.head)
    } else {
      val measureAreaLayout = MeasureAreaLayout.fromFields(fields.toList)
      MeasureAreaTree(Right(measureAreaLayout))
    }
    val measureAreaLayout = MeasureAreaLayout.fromFields(fields.toList)
    MeasureAreaTree(Right(measureAreaLayout))
  }
  def apply(topFields:List[Field[_]], childFields:List[Field[_]]):MeasureAreaTree = {
    MeasureAreaTree(Right(MeasureAreaLayout.fromFields(topFields)), MeasureAreaLayout.fromFields(childFields))
  }
}