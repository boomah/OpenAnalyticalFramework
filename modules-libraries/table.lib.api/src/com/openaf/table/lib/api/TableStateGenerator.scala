package com.openaf.table.lib.api

trait TableStateGenerator {
  def tableStateFromDoubleClick(fields:List[Field[_]], tableData:TableData):TableState
}

class DefaultTableStateGenerator extends TableStateGenerator {
  def tableStateFromDoubleClick(fields:List[Field[_]], tableData:TableData) = {
    val fieldsInUse = tableData.tableState.allFields
    var tableState = tableData.tableState
    fields.foreach(field => {
      if (fieldsInUse.contains(field)) {
        tableState = tableState.remove(field)
      } else {
        if (field.fieldType.isDimension) {
          tableState = tableState.withRowHeaderFields(tableState.tableLayout.rowHeaderFields ::: List(field))
        } else {
          tableState = tableState.withMeasureAreaLayout(tableState.tableLayout.measureAreaLayout.addFieldToRight(field))
        }
      }
    })
    tableState
  }
}