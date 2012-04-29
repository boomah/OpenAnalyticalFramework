package com.openaf.browser

import javafx.geometry.Rectangle2D
import javafx.stage.{Stage, Screen}

case class FrameLocation(rectangle:Rectangle2D, fullScreen:Boolean) {
  def asString = {
    val rectList = List(rectangle.getMinX.toInt, rectangle.getMinY.toInt, rectangle.getWidth.toInt, rectangle.getHeight.toInt)
    rectList.mkString("", ",", ",") + fullScreen
  }
  def valid(screen:Screen=Screen.getPrimary) = {
    if (screen.getVisualBounds.contains(rectangle)) this else FrameLocation.default(screen, fullScreen)
  }
  def x = rectangle.getMinX
  def y = rectangle.getMinY
  def width = rectangle.getWidth
  def height = rectangle.getHeight
  def offSet(offSet:Int) = {
    val rect = new Rectangle2D(rectangle.getMinX + offSet, rectangle.getMinY + offSet, rectangle.getWidth, rectangle.getHeight)
    copy(rectangle = rect).valid()
  }
}

object FrameLocation {
  def apply(string:String):FrameLocation = {
    try {
      val (x :: y :: width :: height :: fullScreen :: Nil) = string.split(",").toList
      new FrameLocation(new Rectangle2D(x.toDouble, y.toDouble, width.toDouble, height.toDouble), fullScreen.toBoolean).valid()
    } catch {
      case t => default()
    }
  }
  def apply(stage:Stage) = new FrameLocation(new Rectangle2D(stage.getX, stage.getY, stage.getWidth, stage.getHeight), stage.isFullScreen)

  def default(screen:Screen=Screen.getPrimary, fullScreen:Boolean=false) = {
    val bounds = screen.getVisualBounds
    val (width, height) = {
      val (desiredWidth, desiredHeight) = (1024, 640)
      (if (desiredWidth < bounds.getWidth) desiredWidth else bounds.getWidth - 100,
        if (desiredHeight < bounds.getHeight) desiredHeight else bounds.getHeight - 100)
    }
    val (x, y) = ((bounds.getWidth - width) / 2, (bounds.getHeight - height) / 2)
    FrameLocation(new Rectangle2D(x, y, width, height), fullScreen)
  }
}
