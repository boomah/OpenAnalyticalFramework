package com.openaf.browser.gui.components

import com.openaf.browser.gui.api.{PageComponentFactory, PageComponent}
import com.openaf.browser.gui.api.PageContext
import com.openaf.browser.gui.pages.{HomePage, UtilsPage, BlankPage}
import collection.mutable
import collection.JavaConversions._
import com.openaf.browser.gui.InternalBrowserCacheKey

class PageComponentCache {
  private val cache = new mutable.WeakHashMap[String,PageComponent]
  private val factoryMap = Map[String,PageComponentFactory](
    HomePage.getClass.getName -> HomePageComponentFactory,
    UtilsPage.getClass.getName -> UtilsPageComponentFactory
  )

  private def pageComponentFactoryOption(pageClassName:String, pageContext:PageContext) = {
    val browserApplications = pageContext.browserCache(InternalBrowserCacheKey.ApplicationsKey).listIterator.toList
    val allPageComponentFactories = browserApplications.map(_.componentFactoryMapWithInitialisation)
      .foldLeft(factoryMap)((map, totalMap) => map ++ totalMap)
    allPageComponentFactories.get(pageClassName)
  }

  private def blankPageComponent(pageContext:PageContext) = {
    cache.getOrElseUpdate(BlankPage.getClass.getName, BlankPageComponentFactory.pageComponent(pageContext))
  }

  def pageComponent(pageClassName:String, pageContext:PageContext):PageComponent = {
    pageComponentFactoryOption(pageClassName, pageContext) match {
      case Some(pageComponentFactory) => cache.getOrElseUpdate(pageClassName, pageComponentFactory.pageComponent(pageContext))
      case _ => blankPageComponent(pageContext)
    }
  }

  def exceptionComponent(pageContext:PageContext) = {
    cache.getOrElseUpdate(classOf[ExceptionPageComponent].getName, ExceptionPageComponentFactory.pageComponent(pageContext))
  }
}
