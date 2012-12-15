package com.openaf.browser.gui.components

import com.openaf.browser.gui.{BrowserActionButton, PageContext}
import javafx.scene.layout.VBox
import javafx.scene.control.{Button, Label}
import javafx.event.{ActionEvent, EventHandler}

class OpenAFApplicationComponent(pageContext:PageContext, applicationName:String,
                                 actionButtons:List[BrowserActionButton]) extends VBox {
  private val nameLabel = new Label(applicationName)

  getChildren.add(nameLabel)

  actionButtons.map(button => {
    val buttonNameButton = new Button(button.name)
    buttonNameButton.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {pageContext.goToPage(button.pageFactory.page)}
    })
    getChildren.add(buttonNameButton)
  })
}