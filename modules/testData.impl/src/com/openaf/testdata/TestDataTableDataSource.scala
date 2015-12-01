package com.openaf.testdata

import com.openaf.table.lib.api.TableState
import com.openaf.table.server._
import com.openaf.table.server.datasources.{DataSourceTable, UnfilteredArrayTableDataSource}
import com.openaf.testdata.api.TestDataTablePageDataFacility._
import com.openaf.testdata.api.{StringWrapper, StringWrapperParser}

import scala.collection.mutable
import scala.util.Random

class TestDataTableDataSource extends UnfilteredArrayTableDataSource {
  private val fieldDefinitions = List(
    IntFieldDefinition(IdField), StringWrapperFieldDefinition, IntFieldDefinition(ScoreField)
  )
  private val fieldIds = fieldDefinitions.map(_.fieldID).toArray
  private val fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard,
    FieldDefinitionGroup("Fields", fieldDefinitions.map(Right(_)))
  ))
  private val data:Array[Array[Any]] = {
    val rand = new Random(0)
    val num = 10
    val people = Array("Nick", "Loz", "Josie", "Rosie")
    val rows = new Array[Array[Any]](num)
    (0 until num).foreach(i => {
      val row = new Array[Any](fieldIds.length)
      rows(i) = row
      row(0) = i
      row(1) = StringWrapper(people(rand.nextInt(people.length)))
      row(2) = rand.nextInt(500)
    })

    rows
  }

  override def dataSourceTable(tableState:TableState) = DataSourceTable(fieldIds, data, fieldDefinitionGroups)
}

case object StringWrapperFieldDefinition extends FieldDefinition {
  override type V = StringWrapper
  override type C = Set[StringWrapper]
  override val defaultField = PersonField
  override val primaryKey = false
  override val ordering = StringWrapperOrdering
  override def combiner = new StringWrapperCombiner
  override def parser = StringWrapperParser
}

case object StringWrapperOrdering extends Ordering[StringWrapper] {
  def compare(stringWrapperX:StringWrapper, stringWrapperY:StringWrapper) = stringWrapperX.string.compareTo(stringWrapperY.string)
}

class StringWrapperCombiner extends Combiner[Set[StringWrapper],StringWrapper] {
  private val set = new mutable.HashSet[StringWrapper]
  def combine(value:StringWrapper) = {set += value}
  override def value = set.toSet
}
