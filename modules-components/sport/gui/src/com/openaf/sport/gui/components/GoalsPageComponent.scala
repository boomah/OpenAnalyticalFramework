package com.openaf.sport.gui.components

import com.openaf.table.gui.api.TablePageComponent
import com.openaf.browser.gui.api.PageComponentFactory
import com.openaf.table.lib.api.FieldID
import javafx.beans.binding.StringBinding
import com.openaf.sport.api.SportPage._

class GoalsPageComponent extends TablePageComponent {
  def nameId = "goalsName"

  override def fieldBindings:Map[FieldID,StringBinding] = {
    Map(PlayerField.id -> stringBindingFromResource(PlayerField.id.id))
  }
}

object GoalsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new GoalsPageComponent
}
