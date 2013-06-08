package com.openaf.browser.gui.api.components

import javafx.scene.layout.BorderPane
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.api.pages.BlankPage
import javafx.scene.text.Text

class BlankPageComponent extends BorderPane with PageComponent {
  type P = BlankPage.type
  type PD = NoPageData.type
  def name = "Blank Page"

  setCenter(new Text("BLANK PAGE"))

  def setup() {}
}

object BlankPageComponentFactory extends PageComponentFactory {
  def pageComponent = new BlankPageComponent
}