package com.openaf.browser.gui.components

import com.openaf.browser.gui.{BrowserCacheKey, PageContext}
import com.openaf.browser.gui.pages.HomePage
import collection.mutable
import collection.JavaConversions._

trait PageComponentFactory {
  def pageComponent(pageContext:PageContext):PageComponent
}

class PageComponentCache {
  private val cache = new mutable.WeakHashMap[String,PageComponent]
  private val factoryMap = Map[String,PageComponentFactory](
    HomePage.getClass.getName -> HomePageComponentFactory
  )

  def pageComponent(pageClassName:String, pageContext:PageContext):PageComponent = {
    val browserApplications = pageContext.browserCache(BrowserCacheKey.BrowserApplicationsKeyWithDefault).listIterator.toList
    val totalFactoryMap = browserApplications.map(_.componentFactoryMap).foldLeft(factoryMap)((map, totalMap) => map ++ totalMap)

    cache.getOrElseUpdate(pageClassName, totalFactoryMap(pageClassName).pageComponent(pageContext))
  }
}
