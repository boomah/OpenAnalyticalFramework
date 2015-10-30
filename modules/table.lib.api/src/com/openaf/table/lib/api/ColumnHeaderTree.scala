package com.openaf.table.lib.api

import ColumnHeaderLayout._

case class ColumnHeaderTree(columnHeaderTreeType:ColumnHeaderTreeType, 
                            childColumnHeaderLayout:ColumnHeaderLayout=ColumnHeaderLayout.Blank) {
  def allFields:List[Field[_]] = {
    (columnHeaderTreeType match {
      case Left(field) => List(field)
      case Right(columnHeaderLayout) => columnHeaderLayout.allFields
    }) ++ childColumnHeaderLayout.allFields
  }
  def paths:List[ColumnHeaderLayoutPath] = {
    columnHeaderTreeType match {
      case Left(field) if childColumnHeaderLayout.columnHeaderTrees.isEmpty => List(ColumnHeaderLayoutPath(List(field)))
      case Left(field) => childColumnHeaderLayout.paths.map(path => ColumnHeaderLayoutPath(field :: path.fields))
      case Right(columnHeaderLayout) => {
        val paths = columnHeaderLayout.paths
        val childPaths = childColumnHeaderLayout.paths
        paths.flatMap(path => childPaths.map(childPath => ColumnHeaderLayoutPath(path.fields ::: childPath.fields)))
      }
    }
  }
  def hasChildren = childColumnHeaderLayout.columnHeaderTrees.nonEmpty

  def normalise:List[ColumnHeaderTree] = {
    val normalisedChildColumnHeaderLayout = childColumnHeaderLayout.normalise
    columnHeaderTreeType match {
      case field@Left(_) => List(ColumnHeaderTree(field, normalisedChildColumnHeaderLayout))
      case Right(columnHeaderLayout) => {
        val normalisedColumnHeaderLayout = columnHeaderLayout.normalise
        val allFields = normalisedColumnHeaderLayout.allFields
        if (allFields.isEmpty) {
          normalisedChildColumnHeaderLayout.columnHeaderTrees
        } else if (allFields.size == 1) {
          List(ColumnHeaderTree(Left(allFields.head), normalisedChildColumnHeaderLayout))
        } else {
          normalisedColumnHeaderLayout.columnHeaderTrees match {
            case columnHeaderTree :: Nil if columnHeaderTree.columnHeaderTreeType.isLeft && 
              columnHeaderTree.childColumnHeaderLayout.allFields.nonEmpty => {
              val newChildColumnAreaTree =
                ColumnHeaderTree(Right(columnHeaderTree.childColumnHeaderLayout), normalisedChildColumnHeaderLayout)
              val newChildColumnHeaderLayout = ColumnHeaderLayout(newChildColumnAreaTree).normalise
              List(ColumnHeaderTree(columnHeaderTree.columnHeaderTreeType, newChildColumnHeaderLayout))
            }
            case _ => List(ColumnHeaderTree(Right(normalisedColumnHeaderLayout), normalisedChildColumnHeaderLayout))
          }
        }
      }
    }
  }

  def remove(fields:Field[_]*) = {
    val newColumnHeaderTreeType = columnHeaderTreeType match {
      case Left(field) if fields.contains(field) => Right(ColumnHeaderLayout.Blank)
      case field@Left(_) => field
      case Right(columnHeaderLayout) => Right(columnHeaderLayout.remove(fields:_*))
    }
    val newChildColumnHeaderLayout = childColumnHeaderLayout.remove(fields:_*)
    copy(columnHeaderTreeType = newColumnHeaderTreeType, childColumnHeaderLayout = newChildColumnHeaderLayout)
  }

  def replaceField(oldField:Field[_], newField:Field[_]) = {
    val newColumnHeaderTreeType = columnHeaderTreeType match {
      case Left(field) if field == oldField => Left(newField)
      case field@Left(_) => field
      case Right(columnHeaderLayout) => Right(columnHeaderLayout.replaceField(oldField, newField))
    }
    val newChildColumnHeaderLayout = childColumnHeaderLayout.replaceField(oldField, newField)
    copy(columnHeaderTreeType = newColumnHeaderTreeType, childColumnHeaderLayout = newChildColumnHeaderLayout)
  }

  def generateFieldKeys(keyIterator:Iterator[Int]):ColumnHeaderTree = {
    val newColumnHeaderTreeType:ColumnHeaderTreeType = columnHeaderTreeType match {
      case Left(field) => Left(field.withKey(ColumnHeaderFieldKey(keyIterator.next)))
      case Right(columnHeaderLayout) => Right(columnHeaderLayout.generateFieldKeys(keyIterator))
    }
    val newChildColumnHeaderLayout = childColumnHeaderLayout.generateFieldKeys(keyIterator)
    copy(columnHeaderTreeType = newColumnHeaderTreeType, childColumnHeaderLayout = newChildColumnHeaderLayout)
  }

  def withDefaultFieldNodeStates:ColumnHeaderTree = {
    val newColumnHeaderTreeType:ColumnHeaderTreeType = columnHeaderTreeType match {
      case Left(field) => Left(field.withDefaultFieldNodeState)
      case Right(columnHeaderLayout) => Right(columnHeaderLayout.withDefaultRendererIds)
    }
    val newChildColumnHeaderLayout = childColumnHeaderLayout.withDefaultRendererIds
    copy(columnHeaderTreeType = newColumnHeaderTreeType, childColumnHeaderLayout = newChildColumnHeaderLayout)
  }
}

object ColumnHeaderTree {
  def apply(field:Field[_]):ColumnHeaderTree = ColumnHeaderTree(Left(field))
  def apply(field:Field[_], childColumnHeaderLayout:ColumnHeaderLayout):ColumnHeaderTree = {
    ColumnHeaderTree(Left(field), childColumnHeaderLayout)
  }
  def apply(fields:Field[_]*):ColumnHeaderTree = {
    if (fields.size == 1) {
      ColumnHeaderTree(fields.head)
    } else {
      val columnHeaderLayout = ColumnHeaderLayout.fromFields(fields.toList)
      ColumnHeaderTree(Right(columnHeaderLayout))
    }
    val columnHeaderLayout = ColumnHeaderLayout.fromFields(fields.toList)
    ColumnHeaderTree(Right(columnHeaderLayout))
  }
  def apply(topFields:List[Field[_]], childFields:List[Field[_]]):ColumnHeaderTree = {
    ColumnHeaderTree(Right(ColumnHeaderLayout.fromFields(topFields)), ColumnHeaderLayout.fromFields(childFields))
  }
}