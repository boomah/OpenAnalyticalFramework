package com.openaf.browser.gui.components

import javafx.scene.layout.VBox
import javafx.scene.control.{Button, Label}
import javafx.event.{ActionEvent, EventHandler}
import com.openaf.browser.gui.api.{PageContext, BrowserActionButton}
import javafx.beans.binding.StringBinding

class OpenAFApplicationComponent(pageContext:PageContext, applicationNameBinding:StringBinding,
                                 actionButtons:List[BrowserActionButton]) extends VBox {
  private val nameLabel = new Label
  nameLabel.textProperty.bind(applicationNameBinding)

  getChildren.add(nameLabel)

  actionButtons.map(button => {
    val buttonNameButton = new Button(button.name)
    buttonNameButton.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {pageContext.goToPage(button.pageFactory.page)}
    })
    getChildren.add(buttonNameButton)
  })
}
