package com.openaf.browser.gui.components

import com.openaf.browser.gui.api.{PageComponentFactory, PageComponent}
import com.openaf.browser.gui.api.BrowserContext
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

  private def pageComponentFactoryOption(pageClassName:String, context:BrowserContext) = {
    val browserApplications = context.cache(InternalBrowserCacheKey.ApplicationsKey).listIterator.toList
    val allPageComponentFactories = browserApplications.map(_.componentFactoryMapWithInitialisation)
      .foldLeft(factoryMap)((map, totalMap) => map ++ totalMap)
    allPageComponentFactories.get(pageClassName)
  }

  private def blankPageComponent(context:BrowserContext) = {
    cache.getOrElseUpdate(BlankPage.getClass.getName, BlankPageComponentFactory.pageComponent(context))
  }

  def pageComponent(pageClassName:String, context:BrowserContext):PageComponent = {
    pageComponentFactoryOption(pageClassName, context) match {
      case Some(pageComponentFactory) => cache.getOrElseUpdate(pageClassName, pageComponentFactory.pageComponent(context))
      case _ => blankPageComponent(context)
    }
  }

  def exceptionComponent(context:BrowserContext) = {
    cache.getOrElseUpdate(classOf[ExceptionPageComponent].getName, ExceptionPageComponentFactory.pageComponent(context))
  }
}
