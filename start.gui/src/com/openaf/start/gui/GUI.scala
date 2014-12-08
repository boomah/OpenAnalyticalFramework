package com.openaf.start.gui

import com.openaf.browser.gui._
import com.google.common.eventbus.EventBus
import com.openaf.gui.utils.GuiUtils
import com.openaf.sport.gui.SportBrowserApplication
import com.openaf.pagemanager.api.ServerContext
import com.openaf.rmi.client.RMIClient

object GUI {
  def main(args:Array[String]) {
    // This is a blocking call so run it on another thread
    run(javafx.application.Application.launch(classOf[BrowserStageManager], "Test Start"))

    val eventBus = new EventBus
    val browserStageManager = BrowserStageManager.waitForBrowserStageManager
    eventBus.register(browserStageManager)

    eventBus.post(OpenAFApplicationAdded(SportBrowserApplication))

    val client = new RMIClient("localhost", 9654)
    client.connectBlocking()

    val serverContext = new ServerContext {
      def facility[T](klass:Class[T]) = {
        println("HER we ade " + klass)
        client.proxy[T](klass)
      }
    }

    GuiUtils.runLater({
      val pageBuilder = new PageBuilder(serverContext)
      browserStageManager.start(pageBuilder)
    })
  }

  private def run(function: => Unit) {
    new Thread(new Runnable {
      def run() {function}
    }).start()
  }
}
