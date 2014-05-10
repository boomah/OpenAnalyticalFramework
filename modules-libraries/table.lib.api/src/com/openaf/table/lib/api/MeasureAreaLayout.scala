package com.openaf.table.lib.api

case class MeasureAreaLayout(measureAreaTrees:List[MeasureAreaTree]) {
  def allFields = measureAreaTrees.flatMap(_.allFields)
  def paths:List[MeasureAreaLayoutPath] = measureAreaTrees.flatMap(_.paths)

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
  type MeasureAreaTreeType = Either[Field[_],MeasureAreaLayout]

  def apply(measureAreaTree:MeasureAreaTree):MeasureAreaLayout = MeasureAreaLayout(List(measureAreaTree))

  def fromFields(fields:List[Field[_]]):MeasureAreaLayout = {
    val measureAreaTrees = fields.map(field => MeasureAreaTree(field))
    MeasureAreaLayout(measureAreaTrees.toList)
  }

  def fromFields(fields:Field[_]*):MeasureAreaLayout = {fromFields(fields.toList)}

  def apply(topField:Field[_], childFields:Seq[Field[_]]=Nil):MeasureAreaLayout = {
    val childMeasureAreaLayout = fromFields(childFields.toList)
    val measureAreaTree = MeasureAreaTree(topField, childMeasureAreaLayout)
    MeasureAreaLayout(measureAreaTree)
  }

  def apply(topFields:Seq[Field[_]], childFields:Seq[Field[_]]):MeasureAreaLayout = {
    MeasureAreaLayout(MeasureAreaTree(topFields.toList, childFields.toList)).normalise
  }
}

case class MeasureAreaLayoutPath(fields:List[Field[_]]) {
  def measureFieldOption = fields.find(_.fieldType.isMeasure)
  def measureFieldIndex = fields.indexWhere(_.fieldType.isMeasure)
}