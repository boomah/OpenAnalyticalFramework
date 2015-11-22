package com.openaf.table.server

import com.openaf.table.lib.api.{Field, FieldID}
import com.openaf.table.server.datasources.PivotData
import java.util

private[server] class TableDataGeneratorSorter(pivotData:PivotData) {
  private def fieldDefinition(id:FieldID) = pivotData.fieldDefinitionGroups.fieldDefinition(id)
  private val tableState = pivotData.tableState

  def sortFilterFieldValues() {
    val filterFieldIDs = tableState.tableLayout.filterFieldIDs
    val filterFieldDefinitions = filterFieldIDs.map(fieldDefinition).toArray
    val filterLookUps = filterFieldIDs.map(pivotData.valueLookUp).toArray

    tableState.filterFields.zipWithIndex.foreach{case (field,i) => {
      val fieldValues = pivotData.fieldValues.values(field)
      val currentFieldDefinition = filterFieldDefinitions(i)
      val fieldDefinition = currentFieldDefinition.transformer(field.transformerType).transformedFieldDefinition(currentFieldDefinition)
      val ordering = fieldDefinition.ordering
      val lookUp = filterLookUps(i).asInstanceOf[Array[fieldDefinition.V]]
      FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
    }}
  }

  def sortRowHeaderAndFieldValues() {
    val rowHeaderFields = tableState.tableLayout.rowHeaderFields.toArray
    val rowHeaderFieldIDs = rowHeaderFields.map(_.id)
    val rowHeaderFieldDefinitions = rowHeaderFields.map(field => {
      val currentFieldDefinition = fieldDefinition(field.id)
      currentFieldDefinition.transformer(field.transformerType).transformedFieldDefinition(currentFieldDefinition)
    })
    val rowHeaderLookUps = rowHeaderFieldIDs.map(pivotData.valueLookUp)

    util.Arrays.sort(
      pivotData.rowHeaderValues,
      new RowHeaderComparator(rowHeaderFields, rowHeaderFieldDefinitions, rowHeaderLookUps)
    )

    tableState.rowHeaderFields.zipWithIndex.foreach{case (field,i) => {
      val fieldValues = pivotData.fieldValues.values(field)
      val fieldDefinition = rowHeaderFieldDefinitions(i)
      val ordering = fieldDefinition.ordering
      val lookUp = rowHeaderLookUps(i).asInstanceOf[Array[fieldDefinition.V]]
      FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
    }}
  }

  def sortColumnHeaderAndFieldValues() {
    val fieldPaths = tableState.columnHeaderLayout.paths
    val pathIndexToFields = new Array[Array[Field[_]]](fieldPaths.length)
    fieldPaths.zipWithIndex.foreach{case (path,i) => pathIndexToFields(i) = path.fields.toArray}
    val columnHeaderFields = tableState.columnHeaderLayout.allFields.toArray
    val columnHeaderFieldKeyFieldDefinitions = new Array[FieldDefinition](columnHeaderFields.length)
    val columnHeaderFieldKeyLookUps = new Array[Array[Any]](columnHeaderFields.length)
    columnHeaderFields.foreach(field => {
      val currentFieldDefinition = fieldDefinition(field.id)
      val transformedFieldDefinition = currentFieldDefinition.transformer(field.transformerType).transformedFieldDefinition(currentFieldDefinition)
      columnHeaderFieldKeyFieldDefinitions(field.key.number) = transformedFieldDefinition
      columnHeaderFieldKeyLookUps(field.key.number) = pivotData.valueLookUp(field.id)
    })

    util.Arrays.sort(
      pivotData.columnHeaderValues,
      new ColumnHeaderComparator(pathIndexToFields, columnHeaderFieldKeyFieldDefinitions, columnHeaderFieldKeyLookUps)
    )

    columnHeaderFields.foreach(field => {
      val fieldValues = pivotData.fieldValues.values(field)
      val fieldDefinition = columnHeaderFieldKeyFieldDefinitions(field.key.number)
      val ordering = fieldDefinition.ordering
      val lookUp = columnHeaderFieldKeyLookUps(field.key.number).asInstanceOf[Array[fieldDefinition.V]]
      FieldValuesSorting.sort(fieldValues, ordering, lookUp, field.sortOrder)
    })
  }
}
