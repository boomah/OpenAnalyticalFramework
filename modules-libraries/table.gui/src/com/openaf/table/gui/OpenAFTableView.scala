package com.openaf.table.gui

import javafx.scene.control.{TableColumn, TableView}
import javafx.beans.property.Property
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.TableData

class OpenAFTableView(tableDataProperty:Property[TableData]) extends TableView {
  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {setUpTableView(newTableData)}
  })

  private def setUpTableView(tableData:TableData) {
    val rowHeaders = tableData.tableState.tableLayout.rowHeaderFields
    val rowHeaderTableColumns = rowHeaders.map(field => new TableColumn(field.displayName))
    getColumns.clear()
    getColumns.addAll(rowHeaderTableColumns :_*)
  }
}
