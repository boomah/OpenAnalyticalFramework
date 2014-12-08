package com.openaf.browser.gui.utils

import java.util.prefs.Preferences
import javafx.stage.{Stage, Screen}
import collection.mutable
import javafx.scene.image.{ImageView, Image}
import com.openaf.browser.gui.shortcutkeys.{LinuxShortCutKeys, WindowsShortCutKeys, OSXShortCutKeys}
import javafx.scene.Node
import javafx.geometry.Insets
import com.openaf.browser.gui.FrameLocation
import com.openaf.gui.utils._
import com.openaf.gui.utils.GuiUtils._

object BrowserUtils {
  val ApplicationName = "applicationName"
  private val FrameLocationName = "frameLocation"
  private val Prefs = Preferences.userNodeForPackage(this.getClass)
  def initialFrameLocation = {
    val frameLocationString = Prefs.get(FrameLocationName, FrameLocation.default().asString)
    FrameLocation(frameLocationString).valid(Screen.getPrimary)
  }
  def storeFrameLocation(stage:Stage) {Prefs.put(FrameLocationName, FrameLocation(stage).asString)}
  def deletePreferences() {Prefs.remove(FrameLocationName)}
  private val ImageMap = new mutable.WeakHashMap[String,Image]
  def icon(iconName:String) = {
    val name = "/com/openaf/browser/gui/resources/" + iconName
    val image = ImageMap.getOrElseUpdate(name, new Image(resourceAsInputStream(name)))
    new ImageView(image)
  }
  private def resourceAsInputStream(name:String) = getClass.getResourceAsStream(name)
  def resource(resource:String) = getClass.getResource(resource).toExternalForm

  lazy val keyMap = {
    OS match {
      case OSX => new OSXShortCutKeys
      case Linux => new LinuxShortCutKeys
      case _ => new WindowsShortCutKeys
    }
  }

  def imageViewOfNode(node:Node) = {
    val image = node.snapshot(null, null)
    new ImageView(image)
  }

  def standardInsets = new Insets(5)
}