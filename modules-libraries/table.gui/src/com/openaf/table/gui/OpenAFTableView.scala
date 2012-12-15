package com.openaf.table.gui

import javafx.scene.control.{TableColumn, TableView}
import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.api.TableData
import javafx.beans.value.{ObservableValue, ChangeListener}

class OpenAFTableView extends TableView {
  private val tableData = new SimpleObjectProperty[TableData]
  def getTableData = tableData.get
  def setTableData(tableData0:TableData) {tableData.set(tableData0)}
  def tableDataProperty = tableData

  tableData.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {setUpTableView(newTableData)}
  })

  private def setUpTableView(tableData:TableData) {
    val rowHeaders = tableData.tableState.tableLayout.rowHeaderFields
    val rowHeaderTableColumns = rowHeaders.map(field => new TableColumn(field.displayName))

    getColumns.addAll(rowHeaderTableColumns :_*)
  }
}
