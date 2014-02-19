package com.openaf.table.gui

import org.scalatest.FunSuite
import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.lib.api._
import javafx.embed.swing.JFXPanel
import GUITestData._

class OpenAFTableViewTest extends FunSuite {
  javax.swing.SwingUtilities.invokeAndWait(new Runnable {def run() {new JFXPanel()}})
  val tableDataProperty = new SimpleObjectProperty[TableData]
  val tableView = new OpenAFTableView(tableDataProperty)

  test("1 column") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout(GenderField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1), Array(2))),
      valueLookUp = GenderValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === "F")
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getText === "M")
    assert(columns.get(1).getColumns.size === 0)
  }
}

