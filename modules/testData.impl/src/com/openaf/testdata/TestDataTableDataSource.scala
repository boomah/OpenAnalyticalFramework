package com.openaf.testdata

import com.openaf.table.lib.api.{Combiner, TableState}
import com.openaf.table.server.datasources.{DataSourceTable, UnfilteredArrayTableDataSource}
import com.openaf.table.server.{FieldDefinition, FieldDefinitionGroup, FieldDefinitionGroups, IntFieldDefinition}
import com.openaf.testdata.api.StringWrapper
import com.openaf.testdata.api.TestDataTablePageDataFacility._

import scala.collection.mutable
import scala.collection.mutable.{Set => MSet}
import scala.util.Random

class TestDataTableDataSource extends UnfilteredArrayTableDataSource {
  private val fieldDefinitions = List(
    IntFieldDefinition(IdField), StringWrapperFieldDefinition
  )
  private val fieldIds = fieldDefinitions.map(_.fieldID).toArray
  private val fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard,
    FieldDefinitionGroup("Fields", fieldDefinitions.map(Right(_)))
  ))
  private val data:Array[Array[Any]] = {
    val rand = new Random(0)
    val num = 10
    val people = Array("Nick", "Loz", "Josie")
    val rows = new Array[Array[Any]](num)
    (0 until num).foreach(i => {
      val row = new Array[Any](fieldIds.length)
      rows(i) = row
      row(0) = i
      row(1) = StringWrapper(people(rand.nextInt(people.length)))
    })

    rows
  }

  override def dataSourceTable(tableState:TableState) = DataSourceTable(fieldIds, data, fieldDefinitionGroups)
}

case object StringWrapperFieldDefinition extends FieldDefinition {
  type V = StringWrapper
  type C = MSet[StringWrapper]
  def defaultField = PersonField
  def primaryKey = false
  def ordering = StringWrapperOrdering
  def combiner = StringWrapperCombiner
}

case object StringWrapperOrdering extends Ordering[StringWrapper] {
  def compare(stringWrapperX:StringWrapper, stringWrapperY:StringWrapper) = stringWrapperX.string.compareTo(stringWrapperY.string)
}

case object StringWrapperCombiner extends Combiner[MSet[StringWrapper],StringWrapper] {
  def initialCombinedValue = new mutable.HashSet[StringWrapper]
  def combine(combinedValue:MSet[StringWrapper], value:StringWrapper) = {
    combinedValue += value
    combinedValue
  }
}