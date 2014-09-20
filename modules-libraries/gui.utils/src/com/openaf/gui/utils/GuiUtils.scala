package com.openaf.gui.utils

import javafx.application.Platform
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.MouseEvent
import java.util.Locale

object GuiUtils {
  def resource(resource:String) = getClass.getResource(resource).toExternalForm

  lazy val OS = {
    val osName = System.getProperty("os.name").toLowerCase
    if (osName.startsWith("mac")) OSX
    else if (osName.startsWith("linux")) Linux
    else if (osName.startsWith("windows")) WindowsUnknown
  }

  def runLater(function: =>Unit) {Platform.runLater(new Runnable {def run() {function}})}
  def checkFXThread() {require(Platform.isFxApplicationThread, "This must be called on the FX Application Thread")}

  def separatorMenuItem = new SeparatorMenuItem

  def isSpecialKeyDown(event:MouseEvent) = {
    event.isShortcutDown || event.isShiftDown || event.isControlDown || event.isAltDown
  }

  def cssFromClassName(klass:Class[_]) = {
    val stringFormat = String.format(
      Locale.UK,
      "%s|%s|%s",
      "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])"
    )
    klass.getSimpleName.replaceAll(stringFormat, "-").toLowerCase
  }
}
