package com.openaf.table.gui

import javafx.beans.binding.StringBinding
import javafx.util.Callback
import com.openaf.table.gui.OpenAFTableView._
import com.openaf.table.lib.api.Field

trait OpenAFCellFactory extends Callback[TableColumnType,OpenAFTableCell] {
  private[gui] var shouldUpdateItem = true
  val tableFields:OpenAFTableFields

  // TODO - the rendering should also bind to the renderer. At the moment when the renderer changes we rebuild the whole component
  protected def rendererBinding(field:Field[_], value:Any):StringBinding = new StringBinding {
    bind(tableFields.localeProperty)
    override def computeValue = {
      val renderer = tableFields.renderers.renderer(field).asInstanceOf[Renderer[Any]]
      renderer.render(value, tableFields.localeProperty.getValue)
    }
  }
}
