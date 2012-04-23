package com.openaf.browser

import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.BorderPane

class FrameManager extends javafx.application.Application {
  def start(stage:Stage) {
    println("HELLO")
    val layout = new BorderPane
    val scene = new Scene(layout, 500, 500)
    stage.setScene(scene)
    stage.show()
  }
}
