package com.openaf.table.gui

import javafx.scene.layout.{RowConstraints, Priority, ColumnConstraints, GridPane}
import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.api.TableData
import javafx.beans.value.{ObservableValue, ChangeListener}

class OpenAFTable extends GridPane {
  setGridLinesVisible(true)

  private val configArea = new ConfigArea
  private val filterFieldsArea = new FilterFieldsArea
  private val rowHeaderFieldsArea = new RowHeaderFieldsArea
  private val measureFieldsArea = new MeasureFieldsArea
  private val tableView = new OpenAFTableView

  private val tableData = new SimpleObjectProperty[TableData]
  def getTableData = tableData.get
  def setTableData(tableData0:TableData) {tableData.set(tableData0)}
  def tableDataProperty = tableData

  tableData.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {setUpTable(newTableData)}
  })

  private def setUpTable(tableData:TableData) {
    tableView.setTableData(tableData)
  }

  {
    add(configArea, 0, 0, 1, 3)
    add(filterFieldsArea, 1, 0, 2, 1)
    add(rowHeaderFieldsArea, 1, 1)
    add(measureFieldsArea, 2, 1)
    add(tableView, 1, 2, 2, 1)

    val blankColumnConstraints = new ColumnConstraints
    val growColumnConstraints = new ColumnConstraints
    growColumnConstraints.setHgrow(Priority.ALWAYS)
    getColumnConstraints.addAll(blankColumnConstraints, growColumnConstraints, growColumnConstraints)

    val blankRowConstraints = new RowConstraints
    val growRowConstraints = new RowConstraints
    growRowConstraints.setVgrow(Priority.ALWAYS)
    getRowConstraints.addAll(blankRowConstraints, blankRowConstraints, growRowConstraints)
  }
}
