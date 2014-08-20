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
    val tableState = TableState(tableLayout)
    val fieldValues = FieldValues(nameFieldValues(NameField))
    val tableValues = TableValues.Empty.copy(fieldValues = fieldValues, valueLookUp = NameValuesLookUp)
    TableData(FieldGroupData, tableState, tableValues, DefaultRenderers)
  }
  val tableDataProperty = new SimpleObjectProperty[TableData](tableData)
  val locale = new SimpleObjectProperty[Locale](Locale.UK)

  test("RetainAllFilter") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    assert(model.filter === RetainAllFilter[String]())
  }

  test("RetainFilter single value") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    model.selectOneValue(1)
    assert(model.filter === RetainFilter[String](Set(Rosie)))
  }

  test("RejectFilter single value") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    model.flipValues(FXCollections.observableArrayList(1))
    assert(model.filter === RejectFilter[String](Set(Rosie)))
  }

  test("RetainFilter after all flip") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    model.flipValues(FXCollections.observableArrayList(0))
    model.flipValues(FXCollections.observableArrayList(2))
    assert(model.filter === RetainFilter[String](Set(Laura)))
  }

  test("RejectFilter after all flip") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    model.flipValues(FXCollections.observableArrayList(0))
    model.flipValues(FXCollections.observableArrayList(0))
    model.flipValues(FXCollections.observableArrayList(1,6))
    assert(model.filter === RejectFilter[String](Set(Rosie, Ally)))
  }

  test("RejectAllFilter") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    model.flipValues(FXCollections.observableArrayList(0))
    assert(model.filter === RejectAllFilter[String]())
  }
}
