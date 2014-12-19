package com.openaf.table.gui

import javafx.scene.control.{Button, ToolBar}
import com.openaf.gui.utils.Icons._

class OpenAFTableToolBarNick extends ToolBar {
  getStyleClass.add("openaf-table-tool-bar")

  // TODO - put in tooltip and actual action
  val clearLayoutButton = new OpenAFTableToolBarButton(ClearLayout)

  getItems.addAll(clearLayoutButton)
}

class OpenAFTableToolBarButton(iconCode:String) extends Button {
  getStyleClass.add("openaf-table-tool-bar-button")
  setGraphic(text(iconCode))
  setFocusTraversable(false)
}
