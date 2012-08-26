package com.openaf.browser.pagecomponents

import javafx.scene.text.Text
import javafx.scene.layout.BorderPane
import com.openaf.browser.{PageContext, PageData}

class HomePageComponent(pageContext:PageContext) extends BorderPane with PageComponent {
  setTop(new Text("Nick"))

  def initialise(pageData:PageData) {}
}

object HomePageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HomePageComponent(pageContext)
}