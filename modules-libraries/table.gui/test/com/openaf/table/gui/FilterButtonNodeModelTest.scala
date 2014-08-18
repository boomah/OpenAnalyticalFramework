package com.openaf.table.gui

import org.scalatest.FunSuite
import com.openaf.table.lib.api._
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import GUITestData._

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

  test("NoFilter") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    assert(model.filter === NoFilter[String]())
  }

  test("Single contains filter") {
    val model = new FilterButtonNodeModel[String](NameField, tableDataProperty, locale)
    model.selectOneValue(1)
    assert(model.filter === ContainsFilter[String](Set(Rosie)))
  }
}
