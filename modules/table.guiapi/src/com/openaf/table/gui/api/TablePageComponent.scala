package com.openaf.table.gui.api

import com.openaf.browser.gui.api.{BrowserCacheKey, PageComponent}
import com.openaf.table.gui.OpenAFTable
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.{TableState, StandardFields, FieldID}
import com.openaf.table.api.{TablePage, TablePageData}
import javafx.beans.binding.StringBinding
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

trait TablePageComponent extends OpenAFTable with PageComponent {
  type PD <: TablePageData
  type P <: TablePage

  override def providesTopBorder = true

  override def initialise() {
    localeProperty.bind(context.cache(BrowserCacheKey.LocaleKey))
  }

  private var doingSetup = false

  def setup() {
    doingSetup = true
    requestTableStateProperty.set(pageData.tableData.tableState)
    tableDataProperty.set(pageData.tableData)

    val fieldBindingsToAdd:Map[FieldID,StringBinding] = pageData.tableData.fieldGroup.fields.map(field => {
      val fieldID = field.id
      val binding = if (StandardFields.AllFieldIDs.contains(fieldID)) {
        val tableClass = classOf[OpenAFTable]
        val location = tableClass.getPackage.getName + ".resources.table"
        stringBindingFromResource(fieldID.id, location, tableClass.getClassLoader)
      } else {
        stringBindingFromResource(fieldID.id)
      }
      fieldID -> binding
    })(collection.breakOut)
    fieldBindings.clear()
    import collection.JavaConversions._
    fieldBindings.putAll(fieldBindingsToAdd)
    doingSetup = false
  }

  requestTableStateProperty.addListener(new ChangeListener[TableState] {
    def changed(observable:ObservableValue[_<:TableState], oldValue:TableState, newValue:TableState) {
      if (!doingSetup) {context.goToPage(page.withTableState(newValue.generateFieldKeys))}
    }
  })

  setOnMousePressed(new EventHandler[MouseEvent] {def handle(event:MouseEvent) {event.consume()}})
}
