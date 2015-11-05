package com.openaf.sport.gui.components

import com.openaf.browser.gui.api.PageComponentFactory
import com.openaf.sport.gui.SportRenderers
import com.openaf.table.gui.api.TablePageComponent

class GoalsPageComponent extends TablePageComponent {
  override def nameId = "goalsName"
  override def defaultRenderers = SportRenderers.GoalPageRenderers
}

object GoalsPageComponentFactory extends PageComponentFactory {
  override def pageComponent = new GoalsPageComponent
}

class RunningPageComponent extends TablePageComponent {
  override def nameId = "runningPage"
  override def defaultRenderers = SportRenderers.RunningPageRenderers
}

object RunningPageComponentFactory extends PageComponentFactory {
  override def pageComponent = new RunningPageComponent
}
