package com.openaf.browser.gui

import animation.{BackOnePageTransition, ForwardOnePageTransition}
import javafx.scene.layout.{StackPane, BorderPane}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import components.{PageComponentCache, PageComponent}
import utils.BrowserUtils
import collection.JavaConversions._
import ref.SoftReference

class Browser(homePage:Page, initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager) extends BorderPane {
  private val content = new StackPane
  setCenter(content)
  private val pageContext = new PageContext(manager.cache, this)
  private val pageComponentCache = new PageComponentCache
  private val currentPagePosition = new SimpleIntegerProperty(-1)
  private val pages = FXCollections.observableArrayList[PageInfo]
  val working = new SimpleBooleanProperty(true)
  val backDisabledProperty = new BooleanBinding {
    bind(currentPagePosition, working)
    def computeValue = !(!working.get && (currentPagePosition.get > 0))
  }
  val forwardDisabledProperty = new BooleanBinding {
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
    def computeValue = working.get || (page(currentPagePosition.get) == homePage)
  }

  private def page(pagePosition:Int) = {
    if (pagePosition != -1) {
      pages.get(pagePosition).page
    } else {
      initialPage
    }
  }

  private def pageID(page:Page) = page.getClass.getName

  private val browserBar = new BrowserBar(this, tabPane, stage)
  setTop(browserBar)

  def backBack() {
    if (!backDisabledProperty.get) {
      val fromPagePosition = currentPagePosition.get
      val pagesToMoveBack = ((fromPagePosition - 1) to 0 by -1)
        .indexWhere(index => pageID(page(index)) != pageID(page(fromPagePosition)))
      if (pagesToMoveBack == 0) {
        back()
      } else {
        val toPagePosition = fromPagePosition - pagesToMoveBack
        goToPage(fromPagePosition, toPagePosition)
      }
    }
  }

  def back() {if (!backDisabledProperty.get) goBackOnePage()}
  def forward() {if (!forwardDisabledProperty.get) goForwardOnePage()}

  def forwardForward() {
    if (!forwardDisabledProperty.get) {
      val fromPagePosition = currentPagePosition.get
      val indexOfDifferentPage = pages.listIterator.toList
        .indexWhere(pageInfo => pageID(pageInfo.page) != pageID(page(fromPagePosition)), fromPagePosition + 1)
      val toPagePosition = if (indexOfDifferentPage == -1) (pages.size - 1) else indexOfDifferentPage
      goToPage(fromPagePosition, toPagePosition)
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

  def home() {if (!homeDisabledProperty.get) goToPage(homePage)}

  private def goBackOnePage() {
    val fromPagePosition = currentPagePosition.get
    val toPagePosition = fromPagePosition - 1
    goToPage(fromPagePosition, toPagePosition)
  }

  private def goForwardOnePage() {
    val fromPagePosition = currentPagePosition.get
    val toPagePosition = fromPagePosition + 1
    goToPage(fromPagePosition, toPagePosition)
  }

  private def shouldAnimate(fromPagePosition:Int, toPagePosition:Int) = {
    (fromPagePosition != -1) && (pageID(page(fromPagePosition)) != pageID(page(toPagePosition)))
  }

  private def animation(fromPagePosition:Int, toPagePosition:Int) = {
    if (toPagePosition > fromPagePosition) {
      ForwardOnePageTransition
    } else {
      BackOnePageTransition
    }
  }

  private def currentPageComponent = {
    if (content.getChildren.size != 1) throw new IllegalStateException("More than one component")
    content.getChildren.get(0).asInstanceOf[PageComponent]
  }

  private def withPageResponse(pageResponse:PageResponse, fromPagePosition:Int, toPagePosition:Int, removeForwardPages:Boolean) {
    BrowserUtils.checkFXThread()
    pageResponse match {
      case SuccessPageResponse(pageData) => {
        val pageInfoToGoTo = pages.get(toPagePosition).copy(softPageResponse = new SoftReference(pageResponse))
        val pageComponentToGoTo = pageComponentCache.pageComponent(pageID(pageInfoToGoTo.page), pageContext)
        pageComponentToGoTo.initialise(
          pageInfoToGoTo.page.asInstanceOf[pageComponentToGoTo.P],
          pageData.asInstanceOf[pageComponentToGoTo.PD],
          pageContext
        )
        if (shouldAnimate(fromPagePosition, toPagePosition)) {
          val fromPageComponent = currentPageComponent
          content.getChildren.add(0, pageComponentToGoTo)
          animation(fromPagePosition, toPagePosition).animate(fromPageComponent, pageComponentToGoTo, onComplete = {
            content.getChildren.removeAll(fromPageComponent)
            working.set(false)
          })
        } else {
          if (content.getChildren.isEmpty) content.getChildren.add(pageComponentToGoTo)
          working.set(false)
        }
        pages.set(toPagePosition, pageInfoToGoTo)
        currentPagePosition.set(toPagePosition)
        if (removeForwardPages) {
          pages.remove(toPagePosition + 1, pages.size)
        }
      }
      case ProblemPageResponse(throwable) => {
        throwable.printStackTrace()
        pages.remove(toPagePosition)
      }
    }
  }

  private def goToPage(fromPagePosition:Int, toPagePosition:Int, removeForwardPages:Boolean=false) {
    val pageInfoToGoTo = pages.get(toPagePosition)
    pageInfoToGoTo.softPageResponse.get match {
      case Some(pageResponse) => withPageResponse(pageResponse, fromPagePosition, toPagePosition, removeForwardPages)
      case _ => {
        working.set(true)
        def withResult(pageResponse:PageResponse) {
          withPageResponse(pageResponse, fromPagePosition, toPagePosition, removeForwardPages)
        }
        manager.pageBuilder.build(pageInfoToGoTo.page, withResult)
      }
    }
  }

  def goToPage(page:Page) {
    val fromPagePosition = currentPagePosition.get
    val toPagePosition = fromPagePosition + 1

    val emptyPageDataSoftReference = new SoftReference(SuccessPageResponse(NoPageData))
    emptyPageDataSoftReference.clear()

    pages.add(toPagePosition, PageInfo(page, emptyPageDataSoftReference))

    goToPage(fromPagePosition, toPagePosition, removeForwardPages = true)
  }

  goToPage(initialPage)
}
