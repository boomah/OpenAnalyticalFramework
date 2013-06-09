package com.openaf.browser.gui.api

trait PageComponentFactory {
  def pageComponent(pageContext:PageContext):PageComponent = {
    val newPageComponent = pageComponent
    newPageComponent.initialise(pageContext)
    newPageComponent
  }
  def pageComponent:PageComponent
}