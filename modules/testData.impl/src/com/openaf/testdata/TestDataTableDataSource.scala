package com.openaf.testdata

import com.openaf.table.lib.api.TableState
import com.openaf.table.server.datasources.{DataSourceTable, UnfilteredArrayTableDataSource}
import com.openaf.table.server.{FieldDefinitionGroup, FieldDefinitionGroups, IntFieldDefinition}
import com.openaf.testdata.api.TestData._

class TestDataTableDataSource extends UnfilteredArrayTableDataSource {
  private val fieldDefinitions = List(
    IntFieldDefinition(IdField)
  )
  private val fieldIds = fieldDefinitions.map(_.fieldID).toArray
  private val fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard,
    FieldDefinitionGroup("Fields", fieldDefinitions.map(Right(_)))
  ))
  private val data:Array[Array[Any]] = {
    val num = 1000
    val rows = new Array[Array[Any]](num)
    (0 until num).foreach(i => {
      val row = new Array[Any](fieldIds.length)
      rows(i) = row
      row(0) = i
    })

    rows
  }

  override def dataSourceTable(tableState:TableState) = DataSourceTable(fieldIds, data, fieldDefinitionGroups)
}
