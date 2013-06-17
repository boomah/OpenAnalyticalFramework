package com.openaf.table.lib.api

case class MeasureAreaLayout(measureAreaTrees:List[MeasureAreaTree]) {
  def allFields = measureAreaTrees.flatMap(_.allFields)

  def normalise:MeasureAreaLayout = {
    val normalisedTrees = measureAreaTrees.flatMap(_.normalise).flatMap(tree => {
      if (!tree.hasChildren && tree.measureAreaTreeType.isRight) {
        tree.measureAreaTreeType.right.get.normalise.measureAreaTrees
      } else {
        List(tree)
      }
    })
    MeasureAreaLayout(normalisedTrees)
  }
}

object MeasureAreaLayout {
  val Blank = MeasureAreaLayout(Nil)
  type MeasureAreaTreeType = Either[Field,MeasureAreaLayout]

  def apply(measureAreaTree:MeasureAreaTree):MeasureAreaLayout = MeasureAreaLayout(List(measureAreaTree))

  def fromFields(fields:List[Field]) = {
    val measureAreaTrees = fields.map(field => MeasureAreaTree(field))
    MeasureAreaLayout(measureAreaTrees)
  }

  def apply(topField:Field, childFields:List[Field]=Nil):MeasureAreaLayout = {
    val childMeasureAreaLayout = fromFields(childFields)
    val measureAreaTree = MeasureAreaTree(topField, childMeasureAreaLayout)
    MeasureAreaLayout(measureAreaTree)
  }

  def apply(topFields:List[Field], childFields:List[Field]):MeasureAreaLayout = {
    MeasureAreaLayout(MeasureAreaTree(topFields, childFields))
  }
}