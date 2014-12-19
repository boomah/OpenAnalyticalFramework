package com.openaf.table.gui

import javafx.scene.layout._
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale
import com.openaf.table.lib.api.{TableState, FieldID, TableData}
import javafx.collections.FXCollections
import javafx.beans.binding.StringBinding
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import com.openaf.gui.utils.{OSX, GuiUtils}

object OpenAFTable {
  def styleSheets = List(getClass.getResource("/com/openaf/table/gui/resources/table.css").toExternalForm)
}

class OpenAFTable extends StackPane {
  protected val tableDataProperty = new SimpleObjectProperty[TableData]
  protected val requestTableStateProperty = new SimpleObjectProperty[TableState]

  private val dragAndDrop = new DragAndDrop

  protected val localeProperty = new SimpleObjectProperty[Locale](Locale.getDefault)
  protected val fieldBindings = FXCollections.observableHashMap[FieldID,StringBinding]
  protected val unmodifiableFieldBindings = FXCollections.unmodifiableObservableMap(fieldBindings)

  private val configArea = new ConfigArea(tableDataProperty, requestTableStateProperty, dragAndDrop,
    localeProperty, unmodifiableFieldBindings)
  private val filterFieldsArea = new FilterFieldsArea(tableDataProperty, requestTableStateProperty, dragAndDrop,
    localeProperty, unmodifiableFieldBindings)
  private val rowHeaderFieldsArea = new RowHeaderFieldsArea(tableDataProperty, requestTableStateProperty,
    dragAndDrop, localeProperty, unmodifiableFieldBindings)
  private val columnHeaderArea = new ColumnHeaderArea(tableDataProperty, requestTableStateProperty, dragAndDrop,
    localeProperty, unmodifiableFieldBindings)
  private val toolBar = new OpenAFTableToolBarNick
  private val tableView = new OpenAFTableView(tableDataProperty, requestTableStateProperty, unmodifiableFieldBindings,
    localeProperty)

  {
    val mainContent = new GridPane
    mainContent.add(configArea, 0, 0, 1, 4)
    mainContent.add(filterFieldsArea, 1, 0, 2, 1)
    mainContent.add(rowHeaderFieldsArea, 1, 1)
    mainContent.add(columnHeaderArea, 2, 1)
    mainContent.add(toolBar, 1, 2, 2, 1)
    mainContent.add(tableView, 1, 3, 2, 1)

    val blankColumnConstraints = new ColumnConstraints
    val growColumnConstraints = new ColumnConstraints
    growColumnConstraints.setHgrow(Priority.ALWAYS)
    mainContent.getColumnConstraints.addAll(blankColumnConstraints, growColumnConstraints, growColumnConstraints)

    val blankRowConstraints = new RowConstraints
    val growRowConstraints = new RowConstraints
    growRowConstraints.setVgrow(Priority.ALWAYS)
    mainContent.getRowConstraints.addAll(blankRowConstraints, blankRowConstraints, blankRowConstraints, growRowConstraints)

    getChildren.addAll(mainContent, dragAndDrop.dragPane)
  }

  addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler[KeyEvent] {
    val isMac = GuiUtils.OS == OSX
    import com.openaf.gui.utils.EnhancedKeyEvent._
    import javafx.scene.input.KeyCode._
    val escapeKeyCombination = keyEvent(ESCAPE)
    val toggleSelectedKeyCombination = if (isMac) shortcut(DIGIT1) else alt(DIGIT1)

    def handle(e:KeyEvent) {
      if (toggleSelectedKeyCombination.matches(e)) {
        configArea.toggleSelected(1)
      } else if (escapeKeyCombination.matches(e) && configArea.isConfigAreaNodeFocused) {
        tableView.requestFocus()
      }
    }
  })
}
