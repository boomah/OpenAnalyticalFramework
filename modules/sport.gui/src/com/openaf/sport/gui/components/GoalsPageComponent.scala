package com.openaf.sport.gui.components

import com.openaf.browser.gui.api.PageComponentFactory
import com.openaf.sport.gui.SportRenderers
import com.openaf.table.gui.api.TablePageComponent

class GoalsPageComponent extends TablePageComponent {
  def nameId = "goalsName"
  override def defaultRenderers = SportRenderers.DefaultRenderers
}

object GoalsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new GoalsPageComponent
}
