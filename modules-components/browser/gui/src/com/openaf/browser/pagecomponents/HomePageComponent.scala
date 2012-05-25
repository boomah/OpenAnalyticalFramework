package com.openaf.browser.pagecomponents

import javafx.scene.text.Text
import javafx.scene.layout.BorderPane
import com.openaf.browser.PageData

class HomePageComponent extends BorderPane with PageComponent {
  setTop(new Text("Nick"))

  def initialise(pageData:ªPageData) {}
}

object HomePageComponentFactory extends PageComponentFactory {
  def pageComponent = new HomePageComponent
}