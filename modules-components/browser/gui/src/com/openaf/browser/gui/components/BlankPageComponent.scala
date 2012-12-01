package com.openaf.browser.gui.components

import javafx.scene.layout.BorderPane
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.pages.BlankPage
import javafx.scene.text.Text
import com.openaf.browser.gui.PageContext

class BlankPageComponent extends BorderPane with PageComponent {
  type P = BlankPage.type
  type PD = NoPageData.type
  def name = "Blank Page"

  setCenter(new Text("BLANK PAGE"))

  def setup() {}
}

object BlankPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new BlankPageComponent
}