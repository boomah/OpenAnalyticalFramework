package com.openaf.browser.gui.components

import javafx.scene.layout.BorderPane
import com.openaf.browser.gui.pages.ManageCachesPage
import com.openaf.browser.gui.{PageContext, NoPageData}
import javafx.scene.control.Label

class ManageCachesPageComponent extends BorderPane with PageComponent {
  type P = ManageCachesPage.type
  type PD = NoPageData.type

  setTop(new Label("Manage Caches"))

  def setup() {}
}

object ManageCachesPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new ManageCachesPageComponent
}
