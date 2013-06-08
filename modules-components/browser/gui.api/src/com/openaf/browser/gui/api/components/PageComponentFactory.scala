package com.openaf.browser.gui.api.components

import com.openaf.browser.gui.api.pages.{BlankPage, UtilsPage, HomePage}
import collection.mutable
import collection.JavaConversions._
import com.openaf.browser.gui.api.{PageContext, BrowserCacheKey}

trait PageComponentFactory {
  def pageComponent(pageContext:PageContext):PageComponent
}

class PageComponentCache {
  private val cache = new mutable.WeakHashMap[String,PageComponent]
  private val factoryMap = Map[String,PageComponentFactory](
    HomePage.getClass.getName -> HomePageComponentFactory,
    UtilsPage.getClass.getName -> UtilsPageComponentFactory
  )

  private def pageComponentFactoryOption(pageClassName:String, pageContext:PageContext) = {
    val browserApplications = pageContext.browserCache(BrowserCacheKey.ApplicationsKey).listIterator.toList
    val allPageComponentFactories = browserApplications.map(_.componentFactoryMap).foldLeft(factoryMap)((map, totalMap) => map ++ totalMap)
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
