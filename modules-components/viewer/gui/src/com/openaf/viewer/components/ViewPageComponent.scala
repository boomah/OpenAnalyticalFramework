package com.openaf.viewer.components

import javafx.scene.layout.BorderPane
import com.openaf.browser.components.{PageComponentFactory, PageComponent}
import com.openaf.browser.{PageContext, PageData}
import javafx.scene.control.Label
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color

class ViewPageComponent extends BorderPane with PageComponent {

  setTop(new Label("THIS IS THE VIEW PAGE COMPONENT"))

  val rect = new Rectangle(10, 10, 200, 200)
  rect.setFill(Color.RED)
  setCenter(rect)

  def initialise(pageData:PageData) {}
}

object ViewPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new ViewPageComponent
}
