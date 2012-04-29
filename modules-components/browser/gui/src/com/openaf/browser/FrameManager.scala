package com.openaf.browser

import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import utils.BrowserUtils
import collection.mutable.ListBuffer

class FrameManager extends javafx.application.Application {
  private val stages = ListBuffer[Stage]()
  private var lastFocusedStage:Stage = _

  def start(stage:Stage) {
    stages += stage
    lastFocusedStage = stage
    val initialFrameLocation = BrowserUtils.initialFrameLocation

    val layout = new BorderPane
    val scene = new Scene(layout)
    stage.setScene(scene)
    stage.setX(initialFrameLocation.x)
    stage.setY(initialFrameLocation.y)
    stage.setWidth(initialFrameLocation.width)
    stage.setHeight(initialFrameLocation.height)
    stage.show()
  }

  override def stop() {
    BrowserUtils.storeFrameLocation(lastFocusedStage)
    System.exit(0)
  }
}
