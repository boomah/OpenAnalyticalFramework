package com.openaf.browser.pagecomponents

import com.openaf.browser.Page
import com.openaf.browser.pages.HomePage
import collection.mutable.WeakHashMap

trait PageComponentFactory {
  def pageComponent:PageComponent
}

class PageComponentCache {
  private val cache = new WeakHashMap[Class[_<:Page],PageComponent]
  private val factoryMap = Map[Class[_<:Page], PageComponentFactory](
    HomePage.getClass -> HomePageComponentFactory
  )

  def pageComponent(page:Page):PageComponent = {
    cache.get(page.getClass) match {
      case Some(pageComponent) => pageComponent
      case _ => factoryMap(page.getClass).pageComponent
    }
  }
}
