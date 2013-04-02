package com.openaf.table.api

case class MeasureAreaLayout(measureAreaTrees:List[MeasureAreaTree]) {
  def allFields = measureAreaTrees.flatMap(_.allFields).toSet
  def normalise:MeasureAreaLayout = MeasureAreaLayout(measureAreaTrees.flatMap(_.normalise))
}

object MeasureAreaLayout {
  val Blank = MeasureAreaLayout(Nil)
  type MeasureAreaTreeType = Either[Field,MeasureAreaLayout]

  def apply(measureAreaTree:MeasureAreaTree):MeasureAreaLayout = MeasureAreaLayout(List(measureAreaTree))

  def fromFields(fields:List[Field]) = {
    val measureAreaTrees = fields.map(field => MeasureAreaTree(field))
    MeasureAreaLayout(measureAreaTrees)
  }

  def apply(topField:Field, bottomFields:List[Field]=Nil):MeasureAreaLayout = {
    val childMeasureAreaLayout = fromFields(bottomFields)
    val measureAreaTree = MeasureAreaTree(topField, childMeasureAreaLayout)
    MeasureAreaLayout(measureAreaTree)
  }
}

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
    println("000 " + (measureAreaTreeType,"---", childMeasureAreaLayout))
    val normalisedChildMeasureAreaLayout = childMeasureAreaLayout.normalise
    println("111 " + normalisedChildMeasureAreaLayout)
    if (normalisedChildMeasureAreaLayout.allFields.isEmpty) {
      measureAreaTreeType match {
        case field@Left(_) => {
          println("222 " + field)
          List(MeasureAreaTree(field))
        }
        case right@Right(measureAreaLayout) => {
          val normalisedMeasureAreaLayout = measureAreaLayout.normalise
          println("xxx " + normalisedMeasureAreaLayout)
          if (normalisedMeasureAreaLayout.measureAreaTrees.forall(_.allFields.size == 1)) {
            println("333 ")
            val r = normalisedMeasureAreaLayout.measureAreaTrees.flatMap(_.allFields).map(field => MeasureAreaTree(field))
            println("xxx1 " + r)
            r
          } else {
            println("444")
            List(MeasureAreaTree(right))
          }
        }
      }
    } else {
      val newMeasureAreaTreeTypeOption = measureAreaTreeType match {
        case field@Left(_) => Some(field)
        case right@Right(measureAreaLayout) => {
          println("aaa")
          val normalisedMeasureAreaLayout = measureAreaLayout.normalise
          val allFields = normalisedMeasureAreaLayout.allFields
          if (allFields.isEmpty) {
            println("bbb")
            None
          } else if (allFields.size == 1) {
            println("ccc")
            Some(Left(measureAreaLayout.allFields.head))
          } else {
            println("ddd " + (normalisedMeasureAreaLayout, "---", normalisedChildMeasureAreaLayout))
            Some(Right(normalisedMeasureAreaLayout))
          }
        }
      }
      newMeasureAreaTreeTypeOption match {
        case Some(newMeasureAreaTreeType) => {
          println("eee " + (newMeasureAreaTreeType, "---", normalisedChildMeasureAreaLayout))
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
}