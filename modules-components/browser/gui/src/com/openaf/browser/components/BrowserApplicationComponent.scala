package com.openaf.browser.components

import com.openaf.browser.{PageContext, BrowserApplication}
import javafx.scene.layout.VBox
import javafx.scene.control.{Button, Label}
import javafx.event.{ActionEvent, EventHandler}

class BrowserApplicationComponent(pageContext:PageContext, browserApplication:BrowserApplication) extends VBox {
  private val nameLabel = new Label(browserApplication.applicationName)

  getChildren.add(nameLabel)

  browserApplication.browserApplicationButtons(pageContext).map(button => {
    val buttonNameButton = new Button(button.name)
    buttonNameButton.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {pageContext.goToPage(button.pageFactory.page)}
    })
    getChildren.add(buttonNameButton)
  })
}
