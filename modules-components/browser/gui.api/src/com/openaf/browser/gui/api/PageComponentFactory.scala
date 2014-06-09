package com.openaf.browser.gui.api

trait PageComponentFactory {
  private[api] var application:OpenAFApplication = _
  final def pageComponent(pageContext:PageContext):PageComponent = {
    val newPageComponent = pageComponent
    newPageComponent.initialise(pageContext, application)
    newPageComponent
  }
  def pageComponent:PageComponent
}