package com.openaf.table.lib.api

case class ColumnHeaderLayout(columnHeaderTrees:List[ColumnHeaderTree]) {
  def allFields = columnHeaderTrees.flatMap(_.allFields)
  def isEmpty = columnHeaderTrees.isEmpty
  def paths:List[ColumnHeaderLayoutPath] = columnHeaderTrees.flatMap(_.paths)
  def columnHeaderLayoutPathBreaks = columnHeaderTrees.map(_.paths.size).scanLeft(0)(_ + _).tail.toSet
  def remove(fields:Field[_]*):ColumnHeaderLayout = {
    copy(columnHeaderTrees = columnHeaderTrees.map(_.remove(fields:_*))).normalise
  }
  def addFieldToRight(field:Field[_]) = {
    copy(columnHeaderTrees = columnHeaderTrees ++ List(ColumnHeaderTree(field))).normalise
  }
  def replaceField(oldField:Field[_], newField:Field[_]):ColumnHeaderLayout = {
    copy(columnHeaderTrees = columnHeaderTrees.map(_.replaceField(oldField, newField)))
  }

  def normalise:ColumnHeaderLayout = {
    val normalisedTrees = columnHeaderTrees.flatMap(_.normalise).flatMap(tree => {
      if (!tree.hasChildren && tree.columnHeaderTreeType.isRight) {
        tree.columnHeaderTreeType.right.get.normalise.columnHeaderTrees
      } else {
        List(tree)
      }
    })
    ColumnHeaderLayout(normalisedTrees)
  }
}

object ColumnHeaderLayout {
  val Blank = ColumnHeaderLayout(Nil)
  type ColumnHeaderTreeType = Either[Field[_],ColumnHeaderLayout]

  def apply(columnHeaderTree:ColumnHeaderTree):ColumnHeaderLayout = ColumnHeaderLayout(List(columnHeaderTree))

  def fromFields(fields:List[Field[_]]):ColumnHeaderLayout = {
    val columnHeaderTrees = fields.map(field => ColumnHeaderTree(field))
    ColumnHeaderLayout(columnHeaderTrees.toList)
  }

  def fromFields(fields:Field[_]*):ColumnHeaderLayout = {fromFields(fields.toList)}

  def apply(topField:Field[_], childFields:Seq[Field[_]]=Nil):ColumnHeaderLayout = {
    val childColumnHeaderLayout = fromFields(childFields.toList)
    val columnHeaderTree = ColumnHeaderTree(topField, childColumnHeaderLayout)
    ColumnHeaderLayout(columnHeaderTree)
  }

  def apply(topFields:Seq[Field[_]], childFields:Seq[Field[_]]):ColumnHeaderLayout = {
    ColumnHeaderLayout(ColumnHeaderTree(topFields.toList, childFields.toList)).normalise
  }
}

case class ColumnHeaderLayoutPath(fields:List[Field[_]]) {
  def measureFieldOption = fields.find(_.fieldType.isMeasure)
  def measureFieldIndex = fields.indexWhere(_.fieldType.isMeasure)
}