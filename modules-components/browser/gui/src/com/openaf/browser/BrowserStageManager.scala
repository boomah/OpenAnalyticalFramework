package com.openaf.browser

import javafx.stage.Stage
import pages.HomePage
import utils.BrowserUtils._
import collection.mutable.ListBuffer
import java.lang.{Boolean => JBoolean}
import javafx.beans.value.{ObservableValue, ChangeListener}

class BrowserStageManager extends javafx.application.Application {
  private val stages = ListBuffer[Stage]()
  private var lastFocusedStage:Stage = _

  private def frameTitle = "OpenAF - " + getParameters.getUnnamed.get(0)

  def start(stage:Stage) {
    createBrowserStage(initialFrameLocation, HomePage)
  }

  override def stop() {
    storeFrameLocation(lastFocusedStage)
    System.exit(0)
  }

  private def createBrowserStage(frameLocation:FrameLocation, initialPage:Page) {
    val stage = new BrowserStage(initialPage, this)
    stages += stage
    stage.setTitle(frameTitle)
    stage.focusedProperty.addListener(new ChangeListener[JBoolean] {
      def changed(observable:ObservableValue[_<:JBoolean], oldValue:JBoolean, newValue:JBoolean) {
        if (newValue) {lastFocusedStage = stage}
      }
    })

    stage.setX(frameLocation.x)
    stage.setY(frameLocation.y)
    stage.setWidth(frameLocation.width)
    stage.setHeight(frameLocation.height)

    stage.show()
  }

  def createBrowserStage(from:Stage, initialPage:Page) {
    createBrowserStage(FrameLocation(from).offSet(30), initialPage)
  }

  def closeBrowserStage(stage:Stage) {
    stage.close()
    stages -= stage
    if (stages.isEmpty) {
      stop()
    }
  }
}
