package com.openaf.browser

import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import utils.BrowserUtils._
import collection.mutable.ListBuffer
import java.lang.{Boolean => JBoolean}
import javafx.beans.value.{ObservableValue, ChangeListener}

class FrameManager extends javafx.application.Application {
  private val stages = ListBuffer[Stage]()
  private var lastFocusedStage:Stage = _

  private def frameTitle = "OpenAF - " + getParameters.getUnnamed.get(0)

  def start(stage:Stage) {
    stages += stage
    val frameLocation = initialFrameLocation

    val layout = new BorderPane
    val scene = new Scene(layout)
    stage.setScene(scene)
    initialiseStage(stage, frameTitle, frameLocation)
    stage.show()
  }

  override def stop() {
    storeFrameLocation(lastFocusedStage)
    System.exit(0)
  }

  private def initialiseStage(stage:Stage, frameTitle:String, frameLocation:FrameLocation) {
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
  }

  private def createStage(from:Stage) = {
    val stage = new Stage
    initialiseStage(stage, frameTitle, FrameLocation(from).offSet(30))
    stage
  }

  private def createAndShowStage(from:Stage) {createStage(from).show()}
}
