package com.openaf.gui.utils

import javafx.scene.text.{Text, Font}

object Icons {
  Font.loadFont(GuiUtils.resource("/com/openaf/gui/utils/resources/icomoon.ttf"), 12)

  val Home = "\ue900"
  val ArrowLeft = "\uea40"
  val ArrowRight = "\uea3c"
  val Stop = "\uea0f"
  val Refresh = "\ue984"
  val Bars = "\ue9bd"
  val Filter = "\ue600"
  val Sigma = "\uea67"
  val ClearLayout = "\ue601"
  val ClearLayout2 = "\uea59"

  def text(iconCode:String) = {
    val text = new Text(iconCode)
    text.getStyleClass.add("icons")
    text
  }
}
