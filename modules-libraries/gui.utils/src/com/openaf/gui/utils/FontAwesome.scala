package com.openaf.gui.utils

import javafx.scene.text.{TextBoundsType, Text, Font}

object FontAwesome extends Enumeration {
  type classOf[FontAwesome] = Value

  Font.loadFont(GuiUtils.resource("/com/openaf/gui/utils/resources/fontawesome-webfont.ttf"), 12)

  val Home = FontAwesome("\uf015")
  val ArrowLeft = FontAwesome("\uf060")
  val ArrowRight = FontAwesome("\uf061")
  val Remove = FontAwesome("\uf00d")
  val Refresh = FontAwesome("\uf021")
  val Cog = FontAwesome("\uf013")
  val Filter = FontAwesome("\uf0b0")
}

case class FontAwesome(text:String)

class FontAwesomeText(fontAwesome:FontAwesome) extends Text(fontAwesome.text) {
  getStyleClass.add("font-awesome")
  setBoundsType(TextBoundsType.VISUAL)
}