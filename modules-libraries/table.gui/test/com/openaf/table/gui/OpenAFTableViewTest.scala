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
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout(GenderField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1), Array(2))),
      valueLookUp = GenderValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === F)
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getText === M)
    assert(columns.get(1).getColumns.size === 0)
  }

  test("2 column (1 on top of the other, top one single value)") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout(GroupField, List(GenderField)))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,1), Array(1,2))),
      valueLookUp = GenderValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getText === F)
    assert(columns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(1).getText === M)
    assert(columns.get(0).getColumns.get(1).getColumns.size === 0)
  }

  test("2 column (1 on top of the other, bottom one single value)") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout(GenderField, List(GroupField)))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,1), Array(2,1))),
      valueLookUp = GenderValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === F)
    assert(columns.get(1).getText === M)
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getText === Friends)
    assert(columns.get(1).getColumns.get(0).getColumns.size === 0)
  }

  test("3 column (1 on top, 2 under)") {
    val tableLayout = TableLayout.Blank.copy(
      columnHeaderLayout = ColumnHeaderLayout(GroupField, List(GenderField, LocationField))
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
    assert(columns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.size === 5)
    assert(columns.get(0).getColumns.get(0).getText === F)
    assert(columns.get(0).getColumns.get(1).getText === M)
    assert(columns.get(0).getColumns.get(2).getText === Edinburgh)
    assert(columns.get(0).getColumns.get(3).getText === London)
    assert(columns.get(0).getColumns.get(4).getText === Manchester)
    assert((0 to 4).exists(col => columns.get(0).getColumns.get(col).getColumns.isEmpty) === true)
  }

  test("0 column, 1 measure") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout(ScoreField))
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
    assert(columns.get(0).getText === Score)
    assert(columns.get(0).getCellData(0) === 34)
  }

  test("1 column, 1 measure (on top of the column)") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout(ScoreField, List(GenderField)))
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
    assert(columns.get(0).getText === Score)
    assert(columns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getText === F)
    assert(columns.get(0).getColumns.get(1).getText === M)
    assert(columns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(1).getCellData(0) === 78)
  }

  test("1 column, 1 measure (underneath the column)") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout(GenderField, List(ScoreField)))
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
    assert(columns.get(0).getText === F)
    assert(columns.get(1).getText === M)
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === Score)
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getText === Score)
    assert(columns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(1).getColumns.get(0).getCellData(0) === 78)
  }

  test("1 column, 1 measure (left of column)") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout.fromFields(ScoreField, GenderField))
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
    assert(columns.get(0).getText === Score)
    assert(columns.get(1).getText === F)
    assert(columns.get(2).getText === M)
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 0)
    assert(columns.get(2).getColumns.size === 0)
    assert(columns.get(0).getCellData(0) === 176)
    assert(columns.get(1).getCellData(0) === NoValue)
    assert(columns.get(2).getCellData(0) === NoValue)
  }

  test("2 column, 1 measure (measure in middle, all on top of each other") {
    val layout = ColumnHeaderLayout(ColumnHeaderTree(GenderField, ColumnHeaderLayout(ScoreField, List(GroupField))))
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = layout)
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
    assert(columns.get(0).getText === M)
    assert(columns.get(1).getText === F)
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === Score)
    assert(columns.get(0).getColumns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getText === Score)
    assert(columns.get(1).getColumns.get(0).getColumns.size === 1)
    assert(columns.get(1).getColumns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(1).getColumns.get(0).getColumns.get(0).getCellData(0) === 78)
    assert(columns.get(1).getColumns.get(0).getColumns.get(0).getColumns.size === 0)
  }

  test("2 column, 1 measure (measure on top, all on top of each other") {
    val layout = ColumnHeaderLayout(ColumnHeaderTree(ScoreField, ColumnHeaderLayout(GroupField, List(GenderField))))
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = layout)
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
    assert(columns.get(0).getText === Score)
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getText === M)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(0).getColumns.get(1).getText === F)
    assert(columns.get(0).getColumns.get(0).getColumns.get(1).getCellData(0) === 78)
    assert(columns.get(0).getColumns.get(0).getColumns.get(1).getColumns.size === 0)
  }

  test("0 column, 2 measure (same next to each other)") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout.fromFields(ScoreField, ScoreField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0)), Array(Array(0))),
      data = Array(Array(Array(176)), Array(Array(176))),
      valueLookUp = ScoreValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === Score)
    assert(columns.get(1).getText === Score)
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 0)
    assert(columns.get(0).getCellData(0) === 176)
    assert(columns.get(1).getCellData(0) === 176)
  }

  test("2 column (same (with two values) next to each other") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout.fromFields(GenderField, GenderField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1), Array(2)), Array(Array(1), Array(2))),
      valueLookUp = GenderValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 4)
    assert(columns.get(0).getText === F)
    assert(columns.get(1).getText === M)
    assert(columns.get(2).getText === F)
    assert(columns.get(3).getText === M)
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 0)
    assert(columns.get(2).getColumns.size === 0)
    assert(columns.get(3).getColumns.size === 0)
    assert(columns.get(0).getCellData(0) === null)
    assert(columns.get(1).getCellData(0) === null)
    assert(columns.get(2).getCellData(0) === null)
    assert(columns.get(3).getCellData(0) === null)
  }

  test("2 column (same (with one value) next to each other") {
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = ColumnHeaderLayout.fromFields(GroupField, GroupField))
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1)), Array(Array(1))),
      valueLookUp = GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === Friends)
    assert(columns.get(1).getText === Friends)
    assert(columns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.size === 0)
    assert(columns.get(0).getCellData(0) === null)
    assert(columns.get(1).getCellData(0) === null)
  }

  test("2 column, 1 measure (over the columns which are next to each other)") {
    val layout = ColumnHeaderLayout(ScoreField, List(GenderField, LocationField))
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = layout)
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0,1), Array(0,2)), Array(Array(0,1), Array(0,2), Array(0,3))),
      data = Array(Array(Array(98,78), Array(Array(49,49,78)))),
      valueLookUp = ScoreValuesLookUp ++ GenderValuesLookUp ++ LocationValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === Score)
    assert(columns.get(0).getColumns.size === 5)
  }

  test("2 column, 2 measure (both measures over the columns and the same, one value for column fields)") {
    val layout = ColumnHeaderLayout(List(ScoreField, ScoreField), List(GroupField, GroupField))
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = layout)
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0,1)), Array(Array(0,1)), Array(Array(0,1)), Array(Array(0,1))),
      data = Array(Array(Array(98)), Array(Array(98)), Array(Array(98)), Array(Array(98))),
      valueLookUp = ScoreValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === Score)
    assert(columns.get(0).getColumns.size === 4)
    assert(columns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.get(1).getText === Friends)
    assert(columns.get(0).getColumns.get(2).getText === Friends)
    assert(columns.get(0).getColumns.get(3).getText === Friends)
    assert(columns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(1).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(2).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(3).getColumns.size === 0)
    assert(columns.get(0).getColumns.get(0).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(1).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(2).getCellData(0) === 98)
    assert(columns.get(0).getColumns.get(3).getCellData(0) === 98)
  }

  test("2 column (columns the same, with one value), 1 measure (on top of both columns)") {
    val layout = ColumnHeaderLayout(ScoreField, List(GroupField, GroupField))
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = layout)
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(0,1)), Array(Array(0,1))),
      data = Array(Array(Array(176)), Array(Array(176))),
      valueLookUp = ScoreValuesLookUp ++ GroupValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 1)
    assert(columns.get(0).getText === Score)
    assert(columns.get(0).getColumns.size === 2)
    assert(columns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.get(1).getText === Friends)
  }

  test("3 column (2 on top of them other, 2 left ones the same)") {
    val layout = ColumnHeaderLayout(List(GroupField, LocationField), List(GroupField))
    val tableLayout = TableLayout.Blank.copy(columnHeaderLayout = layout)
    val tableState = TableState(tableLayout)
    val tableValues = TableValues.Empty.copy(
      columnHeaders = Array(Array(Array(1,1)), Array(Array(1,1))),
      valueLookUp = GroupValuesLookUp ++ LocationValuesLookUp
    )
    val tableData = TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
    tableDataProperty.set(tableData)

    val columns = tableView.getColumns
    assert(columns.size === 2)
    assert(columns.get(0).getText === Friends)
    assert(columns.get(1).getText === Edinburgh)
    assert(columns.get(0).getColumns.size === 1)
    assert(columns.get(1).getColumns.size === 1)
    assert(columns.get(0).getColumns.get(0).getText === Friends)
    assert(columns.get(1).getColumns.get(0).getText === Friends)
    assert(columns.get(0).getColumns.get(0).getColumns.size === 0)
    assert(columns.get(1).getColumns.get(0).getColumns.size === 0)
  }
}

