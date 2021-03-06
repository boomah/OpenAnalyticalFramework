package com.openaf.table.gui

import org.scalatest.FunSuite
import com.openaf.table.lib.api._
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import GUITestData._
import javafx.collections.FXCollections

class FilterButtonNodeModelTest extends FunSuite {
  val tableData = {
    val tableLayout = TableLayout.Blank.copy(rowHeaderFields = List(NameField))
    val tableState = TableState(tableLayout).generateFieldKeys
    val fieldValues = FieldValues(nameFieldValues(NameField))
    val tableValues = TableValues.Empty.copy(fieldValues = fieldValues, valueLookUp = NameValuesLookUp)
    TableData(FieldGroupData, tableState, tableValues, Transformers.Empty, Orderings.Empty)
  }
  val tableDataProperty = new SimpleObjectProperty[TableData](tableData)
  val requestTableStateProperty = new SimpleObjectProperty[RequestTableState](RequestTableState(tableData.tableState))
  val localeProperty = new SimpleObjectProperty[Locale](Locale.UK)
  val renderersProperty = new SimpleObjectProperty[Renderers](new Renderers(DefaultRenderers))
  val tableFields = OpenAFTableFields(tableDataProperty, requestTableStateProperty, null, localeProperty, null, renderersProperty)

  test("RetainAllFilter") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    assert(model.filter === RetainAllFilter[String]())
  }

  test("RetainFilter single value") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    model.selectOneValue(1)
    assert(model.filter === RetainFilter[String](Set(Rosie)))
  }

  test("RejectFilter single value") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    model.flipValues(FXCollections.observableArrayList(1))
    assert(model.filter === RejectFilter[String](Set(Rosie)))
  }

  test("RetainFilter after all flip") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    model.flipValues(FXCollections.observableArrayList(0))
    model.flipValues(FXCollections.observableArrayList(2))
    assert(model.filter === RetainFilter[String](Set(Laura)))
  }

  test("RejectFilter after all flip") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    model.flipValues(FXCollections.observableArrayList(0))
    model.flipValues(FXCollections.observableArrayList(0))
    model.flipValues(FXCollections.observableArrayList(1,6))
    assert(model.filter === RejectFilter[String](Set(Rosie, Ally)))
  }

  test("RejectAllFilter") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    model.flipValues(FXCollections.observableArrayList(0))
    assert(model.filter === RejectAllFilter[String]())
  }

  test("Stays as RejectFilter") {
    val model = new FilterButtonNodeModel[String](NameField, tableFields)
    model.flipValues(FXCollections.observableArrayList(1,2))
    assert(model.filter === RejectFilter[String](Set(Rosie, Laura)))
    model.flipValues(FXCollections.observableArrayList(2))
    assert(model.filter === RejectFilter[String](Set(Rosie)))
  }

  test("First RejectAllFilter should go to RetainFilter") {
    val nameField = NameField.withFilter(RejectAllFilter[String]())
    val newTableData = {
      val tableLayout = TableLayout.Blank.copy(rowHeaderFields = List(nameField))
      val tableState = TableState(tableLayout)
      val fieldValues = FieldValues(nameFieldValues(nameField))
      val tableValues = TableValues.Empty.copy(fieldValues = fieldValues, valueLookUp = NameValuesLookUp)
      TableData(FieldGroupData, tableState, tableValues, Transformers.Empty, Orderings.Empty)
    }
    val newTableDataProperty = new SimpleObjectProperty[TableData](newTableData)
    val model = new FilterButtonNodeModel[String](nameField, tableFields.copy(tableDataProperty = newTableDataProperty))
    model.flipValues(FXCollections.observableArrayList(1))
    assert(model.filter === RetainFilter[String](Set(Rosie)))
  }

  test("First RetainFilter should stay as RetainFilter") {
    val nameField = NameField.withFilter(RetainFilter[String](Set(Rosie)))
    val newTableData = {
      val tableLayout = TableLayout.Blank.copy(rowHeaderFields = List(nameField))
      val tableState = TableState(tableLayout)
      val fieldValues = FieldValues(nameFieldValues(nameField))
      val tableValues = TableValues.Empty.copy(fieldValues = fieldValues, valueLookUp = NameValuesLookUp)
      TableData(FieldGroupData, tableState, tableValues, Transformers.Empty, Orderings.Empty)
    }
    val newTableDataProperty = new SimpleObjectProperty[TableData](newTableData)
    val model = new FilterButtonNodeModel[String](nameField, tableFields.copy(tableDataProperty = newTableDataProperty))
    model.flipValues(FXCollections.observableArrayList(2))
    assert(model.filter === RetainFilter[String](Set(Rosie, Laura)))
  }
}
