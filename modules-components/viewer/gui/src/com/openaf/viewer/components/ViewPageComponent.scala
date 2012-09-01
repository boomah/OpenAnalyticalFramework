package com.openaf.viewer.components

import javafx.scene.layout.BorderPane
import com.openaf.browser.components.{PageComponentFactory, PageComponent}
import com.openaf.browser.{PageContext, PageData}
import javafx.scene.control.Label
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import com.openaf.viewer.ViewPageData

class ViewPageComponent extends BorderPane with PageComponent {

  val label = new Label()

  setTop(label)

  val rect = new Rectangle(10, 10, 200, 200)
  rect.setFill(Color.RED)
  setCenter(rect)

  def initialise(pageData:PageData) {
    label.setText(pageData.asInstanceOf[ViewPageData].text)
  }
}

object ViewPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new ViewPageComponent
}
