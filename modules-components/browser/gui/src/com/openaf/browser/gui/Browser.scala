package com.openaf.browser.gui

import animation.{BackOnePageTransition, ForwardOnePageTransition}
import javafx.scene.layout.{StackPane, BorderPane}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import components.{PageComponentCache, PageComponent}
import utils.BrowserUtils
import ref.SoftReference

class Browser(homePage:Page, initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager) extends BorderPane {
  private val content = new StackPane
  setCenter(content)
  private val pageContext = new PageContext(manager.cache, this)
  private val pageComponentCache = new PageComponentCache
  private val currentPagePosition = new SimpleIntegerProperty(-1)
  private val pages = FXCollections.observableArrayList[PageInfo]
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

  private def currentPage = {
    if (currentPagePosition.get != -1) {
      pages.get(currentPagePosition.get).page
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
      goBackOnePage()
    }
  }

  def redo() {
    if (!redoAndForwardDisabledProperty.get) {
      println("redo")
      goForwardOnePage()
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
      goToPage(homePage)
    }
  }

  private def goBackOnePage() {
    val oldPagePosition = currentPagePosition.get
    currentPagePosition.set(currentPagePosition.get - 1)
    goToCurrentPagePosition(oldPagePosition)
  }

  private def goForwardOnePage() {
    val oldPagePosition = currentPagePosition.get
    currentPagePosition.set(currentPagePosition.get + 1)
    goToCurrentPagePosition(oldPagePosition)
  }

  private def shouldAnimate(oldPagePosition:Int) = {
    (oldPagePosition != -1) && (pages.get(oldPagePosition).page.name != currentPage.name)
  }

  private def animation(oldPagePosition:Int) = {
    if (currentPagePosition.get > oldPagePosition) {
      ForwardOnePageTransition
    } else {
      BackOnePageTransition
    }
  }

  private def currentPageComponent = {
    if (content.getChildren.size != 1) throw new IllegalStateException("More than one component")
    content.getChildren.get(0).asInstanceOf[PageComponent]
  }

  private def withPageResponse(pageResponse:PageResponse, oldPagePosition:Int) {
    BrowserUtils.checkFXThread()
    pageResponse match {
      case SuccessPageResponse(pageData) => {
        val pageInfoToGoTo = pages.get(currentPagePosition.get).copy(softPageResponse = new SoftReference(pageResponse))
        pages.set(currentPagePosition.get, pageInfoToGoTo)
        val pageComponentToGoTo = pageComponentCache.pageComponent(pageInfoToGoTo.page.getClass.getName, pageContext)
        pageComponentToGoTo.initialise(pageInfoToGoTo.page, pageData, pageContext)
        if (shouldAnimate(oldPagePosition)) {
          val fromPageComponent = currentPageComponent
          content.getChildren.add(0, pageComponentToGoTo)
          animation(oldPagePosition).animate(fromPageComponent, pageComponentToGoTo, onComplete = {
            content.getChildren.removeAll(fromPageComponent)
            working.set(false)
          })
        } else {
          if (content.getChildren.isEmpty) content.getChildren.add(pageComponentToGoTo)
          working.set(false)
        }
      }
      case ProblemPageResponse(throwable) => throwable.printStackTrace()
    }
  }

  private def goToCurrentPagePosition(oldPagePosition:Int) {
    val pageInfoToGoTo = pages.get(currentPagePosition.get)
    pageInfoToGoTo.softPageResponse.get match {
      case Some(pageResponse) => withPageResponse(pageResponse, oldPagePosition)
      case _ => {
        working.set(true)
        def withResult(pageResponse:PageResponse) {
          withPageResponse(pageResponse, oldPagePosition)
        }
        manager.pageBuilder.build(pageInfoToGoTo.page, withResult)
      }
    }
  }

  def goToPage(page:Page) {
    pages.remove(currentPagePosition.get + 1, pages.size)

    val emptyPageDataSoftReference = new SoftReference(SuccessPageResponse(PageData.NoPageData))
    emptyPageDataSoftReference.clear()
    pages.add(PageInfo(page, emptyPageDataSoftReference))

    val oldPagePosition = currentPagePosition.get
    currentPagePosition.set(currentPagePosition.get + 1)

    goToCurrentPagePosition(oldPagePosition)
  }

  goToPage(initialPage)
}
