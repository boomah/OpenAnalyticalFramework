package com.openaf.browser.gui

import animation.{BackOnePageTransition, ForwardOnePageTransition}
import javafx.scene.layout.{StackPane, BorderPane}
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.{SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import components.{PageComponentCache, PageComponent}
import utils.BrowserUtils, BrowserUtils._
import collection.JavaConversions._
import ref.SoftReference

class Browser(homePage:Page, initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager) extends BorderPane {
  checkFXThread()
  private val content = new StackPane
  setCenter(content)
  private val pageContext = new PageContext(manager.cache, this)
  private val pageComponentCache = new PageComponentCache
  private val currentPagePosition = new SimpleIntegerProperty(-1)
  private val pages = FXCollections.observableArrayList[PageInfo]
  val working = new SimpleBooleanProperty(true)
  private val animating = new SimpleBooleanProperty(false)
  private var generatingPage:Option[Page] = None
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
    bind(refreshable, working, animating)
    def computeValue = !((!animating.get && working.get && (currentPagePosition.get >= 0)) || (!working.get && refreshable.get))
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
        goToPage(fromPagePosition, toPagePosition, pages.get(toPagePosition))
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
      goToPage(fromPagePosition, toPagePosition, pages.get(toPagePosition))
    }
  }

  def refresh() {
    if (!stopOrRefreshDisabledProperty.get) {
      println("refresh")
    }
  }

  def stop() {
    if (!stopOrRefreshDisabledProperty.get) {
      forceStop()
    }
  }

  private def forceStop() {
    generatingPage = None
    working.set(false)
  }

  def stopOrRefresh(isStop:Boolean) {
    if (isStop) stop() else refresh()
  }

  def home() {if (!homeDisabledProperty.get) goToPage(homePage)}

  private def goBackOnePage() {
    val fromPagePosition = currentPagePosition.get
    val toPagePosition = fromPagePosition - 1
    goToPage(fromPagePosition, toPagePosition, pages.get(toPagePosition))
  }

  private def goForwardOnePage() {
    val fromPagePosition = currentPagePosition.get
    val toPagePosition = fromPagePosition + 1
    goToPage(fromPagePosition, toPagePosition, pages.get(toPagePosition))
  }

  private def shouldAnimate(fromPagePosition:Int, toPage:Page) = {
    (fromPagePosition != -1) && (pageID(page(fromPagePosition)) != pageID(toPage))
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

  private def withPageResponse(pageResponse:PageResponse, fromPagePosition:Int, toPagePosition:Int,
                               pageInfoToGoTo:PageInfo, newPage:Boolean) {
    checkFXThread()
    pageResponse match {
      case SuccessPageResponse(pageData) => {
        val pageInfoWithResponseToGoTo = pageInfoToGoTo.copy(softPageResponse = new SoftReference(pageResponse))
        val pageComponentToGoTo = pageComponentCache.pageComponent(pageID(pageInfoWithResponseToGoTo.page), pageContext)
        pageComponentToGoTo.initialise(
          pageInfoWithResponseToGoTo.page.asInstanceOf[pageComponentToGoTo.P],
          pageData.asInstanceOf[pageComponentToGoTo.PD],
          pageContext
        )

        def tidyUp() {
          if (newPage) {
            pages.add(toPagePosition, pageInfoWithResponseToGoTo)
            pages.remove(toPagePosition + 1, pages.size)
          } else {
            pages.set(toPagePosition, pageInfoWithResponseToGoTo)
          }
          currentPagePosition.set(toPagePosition)
          forceStop()
        }

        if (shouldAnimate(fromPagePosition, pageInfoWithResponseToGoTo.page)) {
          val fromPageComponent = currentPageComponent
          content.getChildren.add(0, pageComponentToGoTo)
          animating.set(true)
          animation(fromPagePosition, toPagePosition).animate(fromPageComponent, pageComponentToGoTo, onComplete = {
            content.getChildren.removeAll(fromPageComponent)
            animating.set(false)
            tidyUp()
          })
        } else {
          if (content.getChildren.isEmpty) content.getChildren.add(pageComponentToGoTo)
          tidyUp()
        }
      }
      case ProblemPageResponse(throwable) => {
        throwable.printStackTrace()
        pages.remove(toPagePosition)
        forceStop()
      }
    }
  }

  private def goToPage(fromPagePosition:Int, toPagePosition:Int, pageInfoToGoTo:PageInfo, newPage:Boolean=false) {
    working.set(true)
    pageInfoToGoTo.softPageResponse.get match {
      case Some(pageResponse) => withPageResponse(pageResponse, fromPagePosition, toPagePosition, pageInfoToGoTo, newPage)
      case _ => {
        generatingPage = Some(pageInfoToGoTo.page)
        def withResult(pageResponse:PageResponse) {
          if (generatingPage == Some(pageInfoToGoTo.page)) {
            withPageResponse(pageResponse, fromPagePosition, toPagePosition, pageInfoToGoTo, newPage)
          }
        }
        manager.pageBuilder.build(pageInfoToGoTo.page, withResult)
      }
    }
  }

  def goToPage(page:Page) {
    checkFXThread()
    if (generatingPage != Some(page)) {
      val fromPagePosition = currentPagePosition.get
      val toPagePosition = fromPagePosition + 1

      val emptyPageDataSoftReference = new SoftReference(SuccessPageResponse(NoPageData))
      emptyPageDataSoftReference.clear()

      val pageInfoToGoTo = PageInfo(page, emptyPageDataSoftReference)

      goToPage(fromPagePosition, toPagePosition, pageInfoToGoTo, newPage = true)
    }
  }

  goToPage(initialPage)
}
