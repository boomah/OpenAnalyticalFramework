package com.openaf.viewer.gui.components

import javafx.scene.layout.BorderPane
import com.openaf.browser.gui.api.{PageComponentFactory, PageComponent}
import javafx.scene.control.{Button, Label}
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import javafx.event.{ActionEvent, EventHandler}
import com.openaf.viewer.api.{ViewerPage, ViewerPageData}

class ViewerPageComponent extends BorderPane with PageComponent {
  type P = ViewerPage
  type PD = ViewerPageData
  def name = "View"

  val label = new Label()

  setTop(label)

  val rect = new Rectangle(10, 10, 200, 200)
  rect.setFill(Color.RED)
  setCenter(rect)

  val button = new Button("Click HERE")
  button.setOnAction(new EventHandler[ActionEvent] {
    def handle(event:ActionEvent) {
      pageContext.goToPage(page.copy(number = page.number + 1))
    }
  })
  setBottom(button)

  def setup() {
    label.setText(pageData.text + " : " + page.number)
  }
}

object ViewerPageComponentFactory extends PageComponentFactory {
  def pageComponent = new ViewerPageComponent
}
