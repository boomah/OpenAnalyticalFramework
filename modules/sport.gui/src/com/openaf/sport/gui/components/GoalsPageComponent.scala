package com.openaf.sport.gui.components

import com.openaf.table.gui.api.TablePageComponent
import com.openaf.browser.gui.api.PageComponentFactory

class GoalsPageComponent extends TablePageComponent {
  def nameId = "goalsName"
}

object GoalsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new GoalsPageComponent
}