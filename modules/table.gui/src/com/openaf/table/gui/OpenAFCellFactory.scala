package com.openaf.table.gui

import javafx.beans.binding.StringBinding
import javafx.util.Callback
import com.openaf.table.gui.OpenAFTableView._
import com.openaf.table.lib.api.Field

trait OpenAFCellFactory extends Callback[TableColumnType,OpenAFTableCell] {
  var shouldUpdateItem = true
  val tableFields:OpenAFTableFields

  protected def rendererBinding(field:Field[_], value:Any):StringBinding = new StringBinding {
    bind(tableFields.renderersProperty, tableFields.localeProperty)
    override def computeValue = {
      val renderer = tableFields.renderersProperty.getValue.renderer(field).asInstanceOf[Renderer[Any]]
      renderer.render(value, tableFields.localeProperty.getValue)
    }
  }
}
