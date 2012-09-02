package com.openaf.viewer.gui.components

import javafx.scene.layout.BorderPane
import com.openaf.browser.gui.components.{PageComponentFactory, PageComponent}
import com.openaf.browser.gui.PageContext
import javafx.scene.control.{Button, Label}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import com.openaf.viewer.gui.{ViewPage, ViewPageData}
import javafx.event.{ActionEvent, EventHandler}

class ViewPageComponent extends BorderPane with PageComponent {
  type P = ViewPage
  type PD = ViewPageData

  val label = new Label()

  setTop(label)

  val rect = new Rectangle(10, 10, 200, 200)
  rect.setFill(Color.RED)
  setCenter(rect)

  val button = new Button("Click HERE")
  button.setOnAction(new EventHandler[ActionEvent] {
    def handle(event:ActionEvent) {
      val viewPage = page
      pageContext.goToPage(viewPage.copy(number = viewPage.number + 1))
    }
  })
  setBottom(button)

  def setup() {
    label.setText(pageData.text + " : " + page.number)
  }
}

object ViewPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new ViewPageComponent
}
