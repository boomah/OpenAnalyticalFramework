package com.openaf.viewer.components

import javafx.scene.layout.BorderPane
import com.openaf.browser.components.{PageComponentFactory, PageComponent}
import com.openaf.browser.{PageContext, PageData}

class ViewPageComponent extends BorderPane with PageComponent {
  def initialise(pageData:PageData) {}
}

object ViewPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new ViewPageComponent
}
