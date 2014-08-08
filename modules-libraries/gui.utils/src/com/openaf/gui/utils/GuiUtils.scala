package com.openaf.gui.utils

import javafx.application.Platform
import javafx.scene.control.SeparatorMenuItem

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
}
