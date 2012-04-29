package com.openaf.browser.utils

import java.util.prefs.Preferences
import com.openaf.browser.FrameLocation
import javafx.stage.{Stage, Screen}

object BrowserUtils {
  private val FrameLocationName = "frameLocation"
  private val Prefs = Preferences.userNodeForPackage(this.getClass)
  def initialFrameLocation = {
    val frameLocationString = Prefs.get(FrameLocationName, FrameLocation.default().asString)
    FrameLocation(frameLocationString).valid(Screen.getPrimary)
  }
  def storeFrameLocation(stage:Stage) {Prefs.put(FrameLocationName, FrameLocation(stage).asString)}
  def deletePreferences() {Prefs.remove(FrameLocationName)}
}