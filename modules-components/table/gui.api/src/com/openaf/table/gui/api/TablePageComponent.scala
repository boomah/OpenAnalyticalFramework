package com.openaf.table.gui.api

import com.openaf.browser.gui.api.{BrowserCacheKey, PageComponent}
import com.openaf.table.gui.OpenAFTable
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.{FieldID, TableData}
import com.openaf.table.api.{TablePage, TablePageData}
import javafx.beans.binding.StringBinding
import java.util.ResourceBundle

trait TablePageComponent extends OpenAFTable with PageComponent {
  type PD <: TablePageData
  type P <: TablePage
  def fieldBindings:Map[FieldID,StringBinding] = Map.empty
  def standardFieldBindings = {
    def standardStringBinding(fieldID:FieldID) = {
      new StringBinding {
        bind(context.cache(BrowserCacheKey.LocaleKey))
        def computeValue = {
          val tablePageComponentClass = classOf[TablePageComponent]
          val location = tablePageComponentClass.getPackage.getName + ".resources.table"
          ResourceBundle.getBundle(
            location, context.cache(BrowserCacheKey.LocaleKey).get, tablePageComponentClass.getClassLoader
          ).getString(fieldID.id)
        }
      }
    }
    import com.openaf.table.lib.api.StandardFields._
    Map(
      CountField.id -> standardStringBinding(CountField.id)
    )
  }

  override def initialise() {
    localeProperty.bind(context.cache(BrowserCacheKey.LocaleKey))
    import collection.JavaConversions._
    fieldBindingsProperty.putAll(standardFieldBindings ++ fieldBindings)
  }

  private var doingSetup = false

  def setup() {
    doingSetup = true
    goingToTableDataProperty.set(pageData.tableData)
    tableDataProperty.set(pageData.tableData)
    doingSetup = false
  }

  goingToTableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observable:ObservableValue[_<:TableData], oldValue:TableData, newValue:TableData) {
      if (!doingSetup) {context.goToPage(page.withTableData(newValue))}
    }
  })
}
