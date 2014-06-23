package com.openaf.table.gui

import org.scalatest.FunSuite
import javafx.beans.property.SimpleObjectProperty
import com.openaf.table.lib.api._
import javafx.embed.swing.JFXPanel
import GUITestData._
import javafx.collections.FXCollections
import javafx.beans.binding.StringBinding

class OpenAFTableViewTest extends FunSuite {
  javax.swing.SwingUtilities.invokeAndWait(new Runnable {def run() {new JFXPanel()}})
  val tableDataProperty = new SimpleObjectProperty[TableData]
  val tableView = new OpenAFTableView(tableDataProperty, FXCollections.emptyObservableMap[FieldID,StringBinding])

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

  test("2 column (1 on top of the other, top one single value)") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout(GroupField, List(GenderField)))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,1), Array(1,2))),
      valueLookUp = GenderValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === "Friends")
    assert(columns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getText === "F")
    assert(columns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(1).getText === "M")
    assert(columns.get(0).getColumns.get(1).getColumns.size === 0)
  }

  test("2 column (1 on top of the other, bottom one single value)") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout(GenderField, List(GroupField)))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,1), Array(2,1))),
      valueLookUp = GenderValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === "F")
    assert(columns.get(1).getText === "M")
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === "Friends")
    assert(columns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getText === "Friends")
    assert(columns.get(1).getColumns.get(0).getColumns.size === 0)
  }

  test("3 column (1 on top, 2 under)") {
    val tableLayout = TableLayout.Blank.copy(
      measureAreaLayout = MeasureAreaLayout(GroupField, List(GenderField, LocationField))
    )
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,1), Array(1,2)), Array(Array(1,1), Array(1,2), Array(1,3))),
      valueLookUp = GenderValuesLookUp ++ LocationValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === "Friends")
    assert(columns.get(0).getColumns.size === 5)
    assert(columns.get(0).getColumns.get(0).getText === "F")
    assert(columns.get(0).getColumns.get(1).getText === "M")
    assert(columns.get(0).getColumns.get(2).getText === "Edinburgh")
    assert(columns.get(0).getColumns.get(3).getText === "London")
    assert(columns.get(0).getColumns.get(4).getText === "Manchester")
    assert((0 to 4).exists(col => columns.get(0).getColumns.get(col).getColumns.isEmpty) === true)
  }

  test("0 column, 1 measure") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout(ScoreField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0))),
      data = Array(Array(Array(34))),
      valueLookUp = ScoreValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === "Score")
    assert(columns.get(0).getCellData(0) === 34)
  }

  test("1 column, 1 measure (on top of the column)") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout(ScoreField, List(GenderField)))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0,1),Array(0,2))),
      data = Array(Array(Array(98, 78))),
      valueLookUp = ScoreValuesLookUp ++ GenderValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === "Score")
    assert(columns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getText === "F")
    assert(columns.get(0).getColumns.get(1).getText === "M")
    assert(columns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(1).getCellData(0) === 78)
  }

  test("1 column, 1 measure (underneath the column)") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout(GenderField, List(ScoreField)))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,0),Array(2,0))),
      data = Array(Array(Array(98, 78))),
      valueLookUp = GenderValuesLookUp ++ ScoreValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === "F")
    assert(columns.get(1).getText === "M")
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === "Score")
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getText === "Score")
    assert(columns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(1).getColumns.get(0).getCellData(0) === 78)
  }

  test("1 column, 1 measure (left of column)") {
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = MeasureAreaLayout.fromFields(ScoreField, GenderField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0)), Array(Array(1),Array(2))),
      data = Array(Array(Array(176)), Array(Array(NoValue, NoValue))),
      valueLookUp = ScoreValuesLookUp ++ GenderValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 3)
    assert(columns.get(0).getText === "Score")
    assert(columns.get(1).getText === "F")
    assert(columns.get(2).getText === "M")
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 0)
    assert(columns.get(2).getColumns.size === 0)
    assert(columns.get(0).getCellData(0) === 176)
    assert(columns.get(1).getCellData(0) === NoValue)
    assert(columns.get(2).getCellData(0) === NoValue)
  }

  test("2 column, 1 measure (measure in middle, all on top of each other") {
    val layout = MeasureAreaLayout(MeasureAreaTree(GenderField, MeasureAreaLayout(ScoreField, List(GroupField))))
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = layout)
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(2,0,1), Array(1,0,1))),
      data = Array(Array(Array(98,78))),
      valueLookUp = GenderValuesLookUp ++ ScoreValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === "M")
    assert(columns.get(1).getText === "F")
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === "Score")
    assert(columns.get(0).getColumns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getText === "Friends")
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getText === "Score")
    assert(columns.get(1).getColumns.get(0).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getColumns.get(0).getText === "Friends")
    assert(columns.get(1).getColumns.get(0).getColumns.get(0).getCellData(0) === 78)
    assert(columns.get(1).getColumns.get(0).getColumns.get(0).getColumns.size === 0)
  }

  test("2 column, 1 measure (measure on top, all on top of each other") {
    val layout = MeasureAreaLayout(MeasureAreaTree(ScoreField, MeasureAreaLayout(GroupField, List(GenderField))))
    val tableLayout = TableLayout.Blank.copy(measureAreaLayout = layout)
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0,1,2), Array(0,1,1))),
      data = Array(Array(Array(98,78))),
      valueLookUp = GenderValuesLookUp ++ ScoreValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === "Score")
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === "Friends")
    assert(columns.get(0).getColumns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getText === "M")
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(0).getColumns.get(1).getText === "F")
    assert(columns.get(0).getColumns.get(0).getColumns.get(1).getCellData(0) === 78)
    assert(columns.get(0).getColumns.get(0).getColumns.get(1).getColumns.size === 0)
  }
}

