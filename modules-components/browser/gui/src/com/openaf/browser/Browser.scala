package com.openaf.browser

import javafx.scene.layout.BorderPane
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import pagecomponents.{PageComponentCache, PageComponent}
import utils.BrowserUtils

class Browser(homePage:Page, initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager) extends BorderPane {
  private val pageContext = new PageContext(manager.cache)
  private val pageComponentCache = new PageComponentCache
  private val currentPagePosition = new SimpleIntegerProperty(-1)
  private val pages = FXCollections.observableArrayList[PageInfo]()
  val working = new SimpleBooleanProperty(true)
  val backAndUndoDisabledProperty = new BooleanBinding {
    bind(currentPagePosition, working)
    def computeValue = !(!working.get && (currentPagePosition.get > 0))
  }
  val redoAndForwardDisabledProperty = new BooleanBinding {
    bind(currentPagePosition, pages, working)
    def computeValue = !(!working.get && (currentPagePosition.get < (pages.size - 1)))
  }
  private val refreshable = new SimpleBooleanProperty(false)
  val stopOrRefreshDisabledProperty = new BooleanBinding {
    bind(refreshable, working)
    def computeValue = !((working.get && (currentPagePosition.get >= 0)) || (!working.get && refreshable.get))
  }
  val homeDisabledProperty = new BooleanBinding {
    bind(working, currentPagePosition, pages)
    def computeValue = working.get || (currentPage == homePage)
  }

  private val currentPage = {
    if (currentPagePosition.get != -1) {
      pages.get(currentPagePosition.get)
    } else {
      initialPage
    }
  }

  private val browserBar = new BrowserBar(this, tabPane, stage)
  setTop(browserBar)

  def back() {
    if (!backAndUndoDisabledProperty.get) {
      println("back")
    }
  }

  def undo() {
    if (!backAndUndoDisabledProperty.get) {
      println("undo")
    }
  }

  def redo() {
    if (!redoAndForwardDisabledProperty.get) {
      println("redo")
    }
  }

  def forward() {
    if (!redoAndForwardDisabledProperty.get) {
      println("forward")
    }
  }

  def refresh() {
    if (!stopOrRefreshDisabledProperty.get) {
      println("refresh")
    }
  }

  def stop() {
    if (!stopOrRefreshDisabledProperty.get) {
      println("Stop")
    }
  }

  def stopOrRefresh(isStop:Boolean) {
    if (isStop) stop() else refresh()
  }

  def home() {
    if (!homeDisabledProperty.get) {
      goTo(homePage)
    }
  }

  private def goTo(page:Page) {
    println("Go to " + page)
    working.set(true)

    def withResult(pageResponse:PageResponse) {
      BrowserUtils.checkFXThread()
      pageResponse match {
        case SuccessPageResponse(pageData) => {
          pages.add(PageInfo(page))
          currentPagePosition.set(currentPagePosition.get + 1)
          val pageComponent = pageComponentCache.pageComponent(page, pageContext)
          pageComponent.pageData = pageData
          showPageComponent(pageComponent)
        }
        case ProblemPageResponse(throwable) => throwable.printStackTrace()
      }
    }

    manager.pageBuilder.build(page, withResult)
  }

  private def showPageComponent(pageComponent:PageComponent) {
    BrowserUtils.checkFXThread()
    setCenter(pageComponent)
    working.set(false)
  }

  goTo(initialPage)
}
