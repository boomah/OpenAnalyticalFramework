package com.openaf.table.gui

import javafx.scene.layout.{RowConstraints, Priority, ColumnConstraints, GridPane}
import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.api.TableData

class OpenAFTable extends GridPane {
  setGridLinesVisible(true)

  private val tableData = new SimpleObjectProperty[TableData]
  def getTableData = tableData.get
  def setTableData(tableData0:TableData) {tableData.set(tableData0)}
  def tableDataProperty = tableData

  private val dragAndDrop = new DragAndDrop

  private val configArea = new ConfigArea(tableData, dragAndDrop)
  private val filterFieldsArea = new FilterFieldsArea
  private val rowHeaderFieldsArea = new RowHeaderFieldsArea(dragAndDrop)
  private val measureFieldsArea = new MeasureFieldsArea
  private val tableView = new OpenAFTableView(tableData)

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
