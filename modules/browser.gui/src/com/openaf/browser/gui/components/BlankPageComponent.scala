package com.openaf.browser.gui.components

import javafx.scene.layout.BorderPane
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.pages.BlankPage
import javafx.scene.text.Text
import com.openaf.browser.gui.api.PageComponentFactory

class BlankPageComponent extends BorderPane with BrowserPageComponent {
  type P = BlankPage.type
  type PD = NoPageData.type
  def nameId = "blankPageName"

  setCenter(new Text("BLANK PAGE"))

  def setup() {}
}

object BlankPageComponentFactory extends PageComponentFactory {
  def pageComponent = new BlankPageComponent
}