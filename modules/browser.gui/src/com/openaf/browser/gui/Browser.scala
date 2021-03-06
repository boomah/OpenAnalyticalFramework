package com.openaf.browser.gui

import javafx.scene.layout.{StackPane, BorderPane}
import javafx.beans.binding.{StringBinding, ObjectBinding, BooleanBinding}
import javafx.beans.property.{SimpleObjectProperty, SimpleBooleanProperty, SimpleIntegerProperty}
import javafx.collections.FXCollections
import com.openaf.gui.utils.GuiUtils

import collection.JavaConversions._
import ref.SoftReference
import com.openaf.pagemanager.api._
import javafx.scene.Node
import com.openaf.browser.gui.api.{BrowserCacheKey, BrowserContext, PageComponent}
import com.openaf.browser.gui.animation.{BackOnePageTransition, ForwardOnePageTransition}
import com.openaf.browser.gui.components.PageComponentCache
import com.openaf.gui.utils.GuiUtils._

class Browser(homePage:Page, initialPage:Page, tabPane:BrowserTabPane, stage:BrowserStage, manager:BrowserStageManager)
  extends BorderPane with BrowserContext {
  checkFXThread()
  def cache = manager.cache
  private val content = new StackPane
  setCenter(content)
  private val pageComponentCache = new PageComponentCache
  private val currentPagePosition = new SimpleIntegerProperty(-1)
  private val pages = FXCollections.observableArrayList[PageInfo]
  private[gui] val working = new SimpleBooleanProperty(true)
  private val animating = new SimpleBooleanProperty(false)
  private[gui] val backDisableProperty = new BooleanBinding {
    bind(currentPagePosition, working)
    def computeValue = !(!working.get && (currentPagePosition.get > 0))
  }
  private[gui] val forwardDisableProperty = new BooleanBinding {
    bind(currentPagePosition, pages, working)
    def computeValue = !(!working.get && (currentPagePosition.get < (pages.size - 1)))
  }
  private val refreshable = new SimpleBooleanProperty(false)
  private[gui] val stopOrRefreshDisableProperty = new BooleanBinding {
    bind(refreshable, working, animating)
    def computeValue = !((!animating.get && working.get && (currentPagePosition.get >= 0)) || (!working.get && refreshable.get))
  }
  private[gui] val homeDisableProperty = new BooleanBinding {
    bind(working, currentPagePosition, pages)
    def computeValue = working.get || (page(currentPagePosition.get) == homePage)
  }
  private val goingToPage = new SimpleObjectProperty[Option[Page]](None)
  private val currentPage = new ObjectBinding[Page] {
    bind(currentPagePosition, pages)
    def computeValue = page(currentPagePosition.get)
  }
  private def pageComponent = {
    try {
      pageComponentCache.pageComponent(pageID(currentPage.get), this)
    } catch {
      case exception:Exception => pageComponentCache.exceptionPageComponent(this)
    }
  }
  private[gui] val nameBinding = new StringBinding {
    bind(currentPage, cache(BrowserCacheKey.LocaleKey))
    def computeValue = pageComponent.name
  }
  def nameChanged() {nameBinding.invalidate()}
  private[gui] val descriptionBinding = new StringBinding {
    bind(currentPage, cache(BrowserCacheKey.LocaleKey))
    def computeValue = pageComponent.description
  }
  def descriptionChanged() {descriptionBinding.invalidate()}
  private[gui] val imageBinding = new ObjectBinding[Node] {
    bind(currentPage)
    def computeValue = pageComponent.image.getOrElse(null)
  }
  def imageChanged() {imageBinding.invalidate()}

  private def page(pagePosition:Int) = {
    if (pagePosition != -1) {
      pages.get(pagePosition).page
    } else {
      initialPage
    }
  }

  private def pageID(page:Page) = page.getClass.getName

  private val browserBar = new BrowserBar(this, tabPane, stage, manager.cache)
  setTop(browserBar)

  private[gui] def backBack() {
    if (!backDisableProperty.get) {
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

  private[gui] def back() {if (!backDisableProperty.get) goBackOnePage()}
  private[gui] def forward() {if (!forwardDisableProperty.get) goForwardOnePage()}

  private[gui] def forwardForward() {
    if (!forwardDisableProperty.get) {
      val fromPagePosition = currentPagePosition.get
      val indexOfDifferentPage = pages.listIterator.toList
        .indexWhere(pageInfo => pageID(pageInfo.page) != pageID(page(fromPagePosition)), fromPagePosition + 1)
      val toPagePosition = if (indexOfDifferentPage == -1) pages.size - 1 else indexOfDifferentPage
      goToPage(fromPagePosition, toPagePosition, pages.get(toPagePosition))
    }
  }

  private def refresh() {
    if (!stopOrRefreshDisableProperty.get) {
      println("refresh")
    }
  }

  private def stop() {
    if (!stopOrRefreshDisableProperty.get) {
      forceStop()
    }
  }

  private def forceStop() {
    goingToPage.set(None)
    working.set(false)
  }

  private[gui] def stopOrRefresh(isStop:Boolean) {
    if (isStop) stop() else refresh()
  }

  private[gui] def home() {if (!homeDisableProperty.get) goToPage(homePage)}

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
    require(content.getChildren.size == 1, "Should only ever contain one component at this point")
    content.getChildren.get(0).asInstanceOf[PageComponent]
  }

  private def withPageResponse(pageResponse:PageResponse, fromPagePosition:Int, toPagePosition:Int,
                               pageInfoToGoTo:PageInfo, newPage:Boolean) {
    checkFXThread()
    val pageInfoWithResponseToGoTo = pageInfoToGoTo.copy(softPageResponse = new SoftReference(pageResponse))
    def setup(pageComponent:PageComponent, pageData:PageData) {
      val id = if (pageComponent.providesTopBorder) {
        "page-component-top-border-provided"
      } else {
        "page-component-top-border-not-provided"
      }
      pageComponent.setId(id)
      pageComponent.setup(
        pageInfoWithResponseToGoTo.page.asInstanceOf[pageComponent.P],
        pageData.asInstanceOf[pageComponent.PD]
      )
    }
    def exceptionPageComponent(exception:Exception) = {
      exception.printStackTrace()
      val pageComponent = pageComponentCache.exceptionPageComponent(this)
      setup(pageComponent, ExceptionPageData(exception))
      pageComponent
    }
    val pageComponentToGoTo = pageResponse match {
      case SuccessPageResponse(pageData) => {
        try {
          val pageComponent = pageComponentCache.pageComponent(pageID(pageInfoWithResponseToGoTo.page), this)
          setup(pageComponent, pageData)
          pageComponent
        } catch {
          case exception:Exception => exceptionPageComponent(exception)
        }
      }
      case ProblemPageResponse(exception) => exceptionPageComponent(exception)
    }

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

  private def goToPage(fromPagePosition:Int, toPagePosition:Int, pageInfoToGoTo:PageInfo, newPage:Boolean=false) {
    working.set(true)
    pageInfoToGoTo.softPageResponse.get match {
      case Some(pageResponse) => withPageResponse(pageResponse, fromPagePosition, toPagePosition, pageInfoToGoTo, newPage)
      case _ => {
        goingToPage.set(Some(pageInfoToGoTo.page))
        def withResult(pageResponse:PageResponse) {
          checkFXThread()
          if (goingToPage.get == Some(pageInfoToGoTo.page)) {
            withPageResponse(pageResponse, fromPagePosition, toPagePosition, pageInfoToGoTo, newPage)
          }
        }
        manager.pageBuilder.build(pageInfoToGoTo.page, withResult)
      }
    }
  }

  def goToPage(page:Page, pageDataOption:Option[PageData]) {
    checkFXThread()
    if (goingToPage.get != Some(page)) {
      println("Going to page: " + page)
      val fromPagePosition = currentPagePosition.get
      val toPagePosition = fromPagePosition + 1

      val emptyPageDataSoftReference = new SoftReference(SuccessPageResponse(NoPageData))
      emptyPageDataSoftReference.clear()

      val pageInfoToGoTo = PageInfo(page, emptyPageDataSoftReference)

      pageDataOption match {
        case Some(pageData) =>
          // runLater here so if things like a context menu kicked this off it will be hidden before any more processing is done
          GuiUtils.runLater(withPageResponse(
            SuccessPageResponse(pageData), fromPagePosition, toPagePosition, pageInfoToGoTo, newPage = true
          ))
        case None => goToPage(fromPagePosition, toPagePosition, pageInfoToGoTo, newPage = true)
      }
    }
  }

  goToPage(initialPage)
}
