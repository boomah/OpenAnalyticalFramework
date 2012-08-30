package com.openaf.browser

import animation.{BackOnePageTransition, ForwardOnePageTransition, BrowserPageAnimation}
import javafx.scene.layout.{StackPane, FlowPane, BorderPane}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import components.{PageComponentCache, PageComponent}
import utils.BrowserUtils, BrowserUtils._

class Browser(homePage:Page, initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager) extends BorderPane {
  private val content = new StackPane
  content.getChildren.add(new FlowPane)
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
      goTo(homePage, BrowserPageAnimation.NoAnimation)
    }
  }

  private def goTo(page:Page, pageAnimation:BrowserPageAnimation) {
    println("Go to " + page)
    working.set(true)

    // TODO - wipe history forward of here

    def withResult(pageResponse:PageResponse) {
      BrowserUtils.checkFXThread()
      val currentImageView = imageViewOfNode(content)
      content.getChildren.add(currentImageView)
      pageResponse match {
        case SuccessPageResponse(pageData) => {
          pages.add(PageInfo(page))
          currentPagePosition.set(currentPagePosition.get + 1)
          val pageComponent = pageComponentCache.pageComponent(page.name, pageContext)
          pageComponent.pageData = pageData
          content.getChildren.add(0, pageComponent)
          pageAnimation.animate(content, currentImageView, imageViewOfNode(pageComponent), onComplete = {
            showPageComponent(pageComponent, page)
          })
        }
        case ProblemPageResponse(throwable) => throwable.printStackTrace()
      }
    }

    manager.pageBuilder.build(page, withResult)
  }

  private def showPageComponent(pageComponent:PageComponent, page:Page) {
    BrowserUtils.checkFXThread()
    content.getChildren.clear()
    content.getChildren.add(pageComponent)
    working.set(false)
  }

  private def transitionToPage(direction:Int, animation:BrowserPageAnimation) {
    val currentImageView = imageViewOfNode(content)
    content.getChildren.add(currentImageView)
    currentPagePosition.set(currentPagePosition.get + direction)
    val pageToGoTo = currentPage
    val pageComponent = pageComponentCache.pageComponent(pageToGoTo.name, pageContext)
    // TODO set the page data
    content.getChildren.add(0, pageComponent)
    animation.animate(content, currentImageView, imageViewOfNode(pageComponent), onComplete = {
      showPageComponent(pageComponent, pageToGoTo)
    })
  }

  private def goBackOnePage() {
    transitionToPage(-1, BackOnePageTransition)
  }

  private def goForwardOnePage() {
    transitionToPage(1, ForwardOnePageTransition)
  }

  def goToPage(page:Page) {
    goTo(page, ForwardOnePageTransition)
  }

  goTo(initialPage, BrowserPageAnimation.NoAnimation)
}
