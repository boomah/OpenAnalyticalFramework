package com.openaf.browser.pagecomponents

import com.openaf.browser.{PageContext, Page}
import com.openaf.browser.pages.HomePage
import collection.mutable

trait PageComponentFactory {
  def pageComponent(pageContext:PageContext):PageComponent
}

class PageComponentCache {
  private val cache = new mutable.WeakHashMap[Class[_<:Page],PageComponent]
  private val factoryMap = Map[Class[_<:Page], PageComponentFactory](
    HomePage.getClass -> HomePageComponentFactory
  )

  def pageComponent(page:Page, pageContext:PageContext):PageComponent = {
    cache.get(page.getClass) match {
      case Some(pageComponent) => pageComponent
      case _ => factoryMap(page.getClass).pageComponent(pageContext)
    }
  }
}
