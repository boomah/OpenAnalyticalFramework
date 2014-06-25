package com.openaf.table.gui

import javafx.scene.layout.{RowConstraints, Priority, ColumnConstraints, GridPane}
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{FieldID, TableData}
import javafx.collections.FXCollections
import javafx.beans.binding.StringBinding

object OpenAFTable {
  def styleSheets = List(getClass.getResource("/com/openaf/table/gui/resources/table.css").toExternalForm)
}

class OpenAFTable extends GridPane {
  setGridLinesVisible(true)

  val tableDataProperty = new SimpleObjectProperty[TableData]
  val goingToTableDataProperty = new SimpleObjectProperty[TableData]

  private val dragAndDrop = new DragAndDrop

  val localeProperty = new SimpleObjectProperty[Locale](Locale.getDefault)
  val fieldBindings = FXCollections.observableHashMap[FieldID,StringBinding]
  val unmodifiableFieldBindings = FXCollections.unmodifiableObservableMap(fieldBindings)

  private val configArea = new ConfigArea(goingToTableDataProperty, dragAndDrop, localeProperty, unmodifiableFieldBindings)
  private val filterFieldsArea = new FilterFieldsArea(goingToTableDataProperty, dragAndDrop, localeProperty, unmodifiableFieldBindings)
  private val rowHeaderFieldsArea = new RowHeaderFieldsArea(goingToTableDataProperty, dragAndDrop, localeProperty, unmodifiableFieldBindings)
  private val measureFieldsArea = new MeasureFieldsArea(goingToTableDataProperty, dragAndDrop, localeProperty, unmodifiableFieldBindings)
  private val tableView = new OpenAFTableView(tableDataProperty, unmodifiableFieldBindings)

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
