package com.openaf.browser.gui.api.components

import javafx.scene.layout.{HBox, BorderPane}
import com.openaf.browser.gui.api.pages.BlankPage
import com.openaf.pagemanager.api.ExceptionPageData
import javafx.scene.control.{Button, TextArea}
import com.google.common.base.Throwables
import com.openaf.browser.gui.api.utils.BrowserUtils
import javafx.geometry.Pos
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.input.{ClipboardContent, Clipboard}

class ExceptionPageComponent extends BorderPane with PageComponent {
  type P = BlankPage.type
  type PD = ExceptionPageData
  def name = "Exception"

  private val textArea = new TextArea
  private val copyToClipboardButton = new Button("Copy to Clipboard")
  copyToClipboardButton.setOnAction(new EventHandler[ActionEvent] {
    def handle(e:ActionEvent) {
      val clipboard = Clipboard.getSystemClipboard
      val clipboardContent = new ClipboardContent
      clipboardContent.putString(textArea.getText)
      clipboard.setContent(clipboardContent)
    }
  })
  private val buttonPanel = new HBox
  buttonPanel.setPadding(BrowserUtils.standardInsets)
  buttonPanel.setAlignment(Pos.CENTER_RIGHT)
  buttonPanel.getChildren.add(copyToClipboardButton)
  setCenter(textArea)
  setBottom(buttonPanel)

  def setup() {
    val rootCause = Throwables.getRootCause(pageData.exception)
    val exceptionString = Throwables.getStackTraceAsString(rootCause)
    textArea.setText(exceptionString)
    copyToClipboardButton.setDefaultButton(true)
  }
}

object ExceptionPageComponentFactory extends PageComponentFactory {
  def pageComponent = new ExceptionPageComponent
}