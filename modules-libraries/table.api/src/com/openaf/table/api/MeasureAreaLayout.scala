package com.openaf.table.api

case class MeasureAreaLayout(measureAreaTrees:List[MeasureAreaTree]) {
  def allMeasureAreaFieldDetails = measureAreaTrees.flatMap(_.allMeasureAreaFieldDetails).toSet
  def allFields = allMeasureAreaFieldDetails.map(_.field)
}

object MeasureAreaLayout {
  val Blank = MeasureAreaLayout(Nil)
  type MeasureAreaTreeType = Either[MeasureAreaField,MeasureAreaLayout]

  def apply(measureAreaTree:MeasureAreaTree):MeasureAreaLayout = MeasureAreaLayout(List(measureAreaTree))

  def fromKeyFields(keyFields:List[Field]) = {
    val measureAreaTrees = keyFields.map(keyField => MeasureAreaTree(MeasureAreaField(keyField, measure=false)))
    MeasureAreaLayout(measureAreaTrees)
  }

  def apply(measureField:Field, keyFields:List[Field]=Nil):MeasureAreaLayout = {
    val measureAreaFieldDetails = MeasureAreaField(measureField, measure=true)
    val childMeasureAreaLayout = fromKeyFields(keyFields)
    val measureAreaTree = MeasureAreaTree(measureAreaFieldDetails, childMeasureAreaLayout)
    MeasureAreaLayout(measureAreaTree)
  }
}

import MeasureAreaLayout._

case class MeasureAreaField(field:Field, measure:Boolean)

case class MeasureAreaTree(measureAreaTreeType:MeasureAreaTreeType, childMeasureAreaLayout:MeasureAreaLayout=MeasureAreaLayout.Blank) {
  def allMeasureAreaFieldDetails:Set[MeasureAreaField] = {
    (measureAreaTreeType match {
      case Left(measureAreaFieldDetails) => Set(measureAreaFieldDetails)
      case Right(measureAreaLayout) => measureAreaLayout.allMeasureAreaFieldDetails
    }) ++ childMeasureAreaLayout.allMeasureAreaFieldDetails
  }
  def hasChildren = childMeasureAreaLayout.measureAreaTrees.nonEmpty
}

object MeasureAreaTree {
  def apply(measureAreaFieldDetails:MeasureAreaField):MeasureAreaTree = MeasureAreaTree(Left(measureAreaFieldDetails))
  def apply(measureAreaFieldDetails:MeasureAreaField, childMeasureAreaLayout:MeasureAreaLayout):MeasureAreaTree = {
    MeasureAreaTree(Left(measureAreaFieldDetails), childMeasureAreaLayout)
  }
}