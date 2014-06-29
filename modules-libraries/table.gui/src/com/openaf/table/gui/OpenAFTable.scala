package com.openaf.table.gui

import javafx.scene.layout._
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{DefaultTableStateGenerator, TableStateGenerator, FieldID, TableData}
import javafx.collections.FXCollections
import javafx.beans.binding.StringBinding

object OpenAFTable {
  def styleSheets = List(getClass.getResource("/com/openaf/table/gui/resources/table.css").toExternalForm)
}

class OpenAFTable extends StackPane {
  val tableDataProperty = new SimpleObjectProperty[TableData]
  val goingToTableDataProperty = new SimpleObjectProperty[TableData]
  val tableStateGeneratorProperty = new SimpleObjectProperty[TableStateGenerator](new DefaultTableStateGenerator)

  private val dragAndDrop = new DragAndDrop

  val localeProperty = new SimpleObjectProperty[Locale](Locale.getDefault)
  val fieldBindings = FXCollections.observableHashMap[FieldID,StringBinding]
  val unmodifiableFieldBindings = FXCollections.unmodifiableObservableMap(fieldBindings)

  private val configArea = new ConfigArea(goingToTableDataProperty, dragAndDrop, localeProperty,
    unmodifiableFieldBindings, tableStateGeneratorProperty)
  private val filterFieldsArea = new FilterFieldsArea(goingToTableDataProperty, dragAndDrop, localeProperty,
    unmodifiableFieldBindings, tableStateGeneratorProperty)
  private val rowHeaderFieldsArea = new RowHeaderFieldsArea(goingToTableDataProperty, dragAndDrop, localeProperty,
    unmodifiableFieldBindings, tableStateGeneratorProperty)
  private val measureFieldsArea = new MeasureFieldsArea(goingToTableDataProperty, dragAndDrop, localeProperty,
    unmodifiableFieldBindings, tableStateGeneratorProperty)
  private val tableView = new OpenAFTableView(tableDataProperty, unmodifiableFieldBindings)

  {
    val mainContent = new GridPane
    mainContent.add(configArea, 0, 0, 1, 3)
    mainContent.add(filterFieldsArea, 1, 0, 2, 1)
    mainContent.add(rowHeaderFieldsArea, 1, 1)
    mainContent.add(measureFieldsArea, 2, 1)
    mainContent.add(tableView, 1, 2, 2, 1)

    val blankColumnConstraints = new ColumnConstraints
    val growColumnConstraints = new ColumnConstraints
    growColumnConstraints.setHgrow(Priority.ALWAYS)
    mainContent.getColumnConstraints.addAll(blankColumnConstraints, growColumnConstraints, growColumnConstraints)

    val blankRowConstraints = new RowConstraints
    val growRowConstraints = new RowConstraints
    growRowConstraints.setVgrow(Priority.ALWAYS)
    mainContent.getRowConstraints.addAll(blankRowConstraints, blankRowConstraints, growRowConstraints)

    getChildren.addAll(mainContent, dragAndDrop.dragPane)
  }
}
