package com.openaf.browser.gui.components

import javafx.scene.layout.VBox
import javafx.scene.control.{Button, Label}
import javafx.event.{ActionEvent, EventHandler}
import com.openaf.browser.gui.api.{OpenAFApplication, BrowserContext, BrowserActionButton}

import com.openaf.browser.gui.binding.ApplicationLocaleStringBinding
import com.openaf.browser.gui.utils.BrowserUtils

class OpenAFApplicationComponent(application:OpenAFApplication, context:BrowserContext) extends VBox {
  {
    val nameLabel = new Label
    val nameLabelBinding = new ApplicationLocaleStringBinding(BrowserUtils.ApplicationName, application, context.cache)
    nameLabel.textProperty.bind(nameLabelBinding)

    getChildren.add(nameLabel)

    val actionButtons = application.applicationButtons(context)
    actionButtons.map(actionButton => {
      val button = new Button
      val buttonLabelBinding = new ApplicationLocaleStringBinding(actionButton.nameId, application, context.cache)
      button.textProperty.bind(buttonLabelBinding)
      button.setOnAction(new EventHandler[ActionEvent] {
        def handle(event:ActionEvent) {context.goToPage(actionButton.pageFactory.page)}
      })

      getChildren.add(button)
    })
  }
}
