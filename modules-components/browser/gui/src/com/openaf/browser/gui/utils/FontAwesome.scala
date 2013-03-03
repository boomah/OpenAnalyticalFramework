package com.openaf.browser.gui.utils

import javafx.scene.text.Font
import javafx.scene.control.Label

object FontAwesome extends Enumeration {
  type classOf[FontAwesome] = Value

  Font.loadFont(BrowserUtils.resource("/com/openaf/browser/gui/resources/fontawesome-webfont.ttf"), 12)

  val Home = FontAwesome("\uf015")
  val ArrowLeft = FontAwesome("\uf060")
  val ArrowRight = FontAwesome("\uf061")
  val Remove = FontAwesome("\uf00d")
  val Refresh = FontAwesome("\uf021")
  val Cog = FontAwesome("\uf013")
}

case class FontAwesome(text:String)

class FontAwesomeLabel(fontAwesome:FontAwesome) extends Label(fontAwesome.text) {
  getStyleClass.add("font-awesome")
}