package com.openaf.browser.gui.components

import com.openaf.browser.gui.{NoPageData, PageContext}
import javafx.scene.layout.BorderPane
import com.openaf.browser.gui.pages.UtilsPage
import javafx.scene.control.Label

class UtilsPageComponent(pageContext:PageContext) extends BorderPane with PageComponent {
  type P = UtilsPage.type
  type PD = NoPageData.type

  setTop(new Label("Utils Page"))

  def setup() {}
}

object UtilsPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new UtilsPageComponent(pageContext)
}
