package com.openaf.browser.components

import com.openaf.browser.{BrowserCacheKey, PageContext}
import com.openaf.browser.pages.HomePage
import collection.mutable
import collection.JavaConversions._

trait PageComponentFactory {
  def pageComponent(pageContext:PageContext):PageComponent
}

class PageComponentCache {
  private val cache = new mutable.WeakHashMap[String,PageComponent]
  private val factoryMap = Map[String,PageComponentFactory](
    HomePage.name -> HomePageComponentFactory
  )

  def pageComponent(pageName:String, pageContext:PageContext):PageComponent = {
    val browserApplications = pageContext.browserCache(BrowserCacheKey.BrowserApplicationsKeyWithDefault).listIterator.toList
    val totalFactoryMap = browserApplications.map(_.componentFactoryMap).foldLeft(factoryMap)((map, totalMap) => map ++ totalMap)

    cache.get(pageName) match {
      case Some(pageComponent) => pageComponent
      case _ => totalFactoryMap(pageName).pageComponent(pageContext)
    }
  }
}
