package com.openaf.browser.gui.api

import javafx.stage.Stage
import collection.mutable.ListBuffer
import java.lang.{Boolean => JBoolean}
import javafx.beans.value.{ObservableValue, ChangeListener}
import java.util.concurrent.CountDownLatch
import com.google.common.eventbus.Subscribe
import com.openaf.pagemanager.api.Page
import javafx.application.{Application => JApplication}
import com.openaf.browser.gui.api.utils.BrowserUtils,BrowserUtils._
import com.openaf.browser.gui.api.pages.HomePage

object BrowserStageManager {
  private val browserCountDownLatch = new CountDownLatch(1)
  private var browserStageManager:BrowserStageManager = _

  def waitForBrowserStageManager:BrowserStageManager = {
    browserCountDownLatch.await()
    browserStageManager
  }

  private def setBrowserStageManager(browserStageManager0:BrowserStageManager) {
    browserStageManager = browserStageManager0
    browserCountDownLatch.countDown()
  }
}

class BrowserStageManager extends JApplication {
  private val stages = ListBuffer[Stage]()
  private var lastFocusedStage:Stage = _
  val cache = new BrowserCache
  var pageBuilder:PageBuilder = _
  addStyleSheetsToAllScenes(List(resource("/com/openaf/browser/gui/api/resources/openaf.css")))

  private def frameTitle = "OpenAF - " + getParameters.getUnnamed.get(0)

  def start(stage:Stage) {
    JApplication.setUserAgentStylesheet(JApplication.STYLESHEET_MODENA)
  }

  def start(pageBuilder0:PageBuilder) {
    pageBuilder = pageBuilder0
    createStage(initialFrameLocation, HomePage)
  }

  override def stop() {
    storeFrameLocation(lastFocusedStage)
    System.exit(0)
  }

  private def createStage(frameLocation:FrameLocation, initialPage:Page) {
    val stage = new BrowserStage(HomePage, initialPage, this)
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

  def createStage(from:Stage, initialPage:Page) {
    createStage(FrameLocation(from).offSet(30), initialPage)
  }

  def closeBrowserStage(stage:Stage) {
    stage.close()
    stages -= stage
    if (stages.isEmpty) {
      stop()
    }
  }

  private def addStyleSheetsToAllScenes(styleSheets:List[String]) {
    println("Add style sheets : " + styleSheets)
    styleSheets.foreach(styleSheet => {
      // TODO - this is private api - move to new package when it is changed to public
      com.sun.javafx.css.StyleManager.getInstance.addUserAgentStylesheet(styleSheet)
    })
  }

  private def removeStyleSheetsFromAllScenes(styleSheets:List[String]) {
    println("Remove style sheets : " + styleSheets)
    // TODO - put this in
  }

  @Subscribe def openAFApplicationAdded(openAFApplication:OpenAFApplicationAdded) {
    runLater({
      val newOpenAFApplication = openAFApplication.openAFApplication
      addStyleSheetsToAllScenes(newOpenAFApplication.styleSheets)
      val currentApplications = cache(BrowserCacheKey.BrowserApplicationsKeyWithDefault)
      currentApplications.add(newOpenAFApplication)
    })
  }

  @Subscribe def openAFApplicationRemoved(openAFApplication:OpenAFApplicationRemoved) {
    runLater({
      val removedOpenAFApplication = openAFApplication.openAFApplication
//      removeStyleSheetsFromAllScenes(removedOpenAFApplication.styleSheets)
      val currentApplications = cache(BrowserCacheKey.BrowserApplicationsKeyWithDefault)
      currentApplications.remove(removedOpenAFApplication)
    })
  }

  BrowserStageManager.setBrowserStageManager(this)
}

case class OpenAFApplicationAdded(openAFApplication:OpenAFApplication)
case class OpenAFApplicationRemoved(openAFApplication:OpenAFApplication)