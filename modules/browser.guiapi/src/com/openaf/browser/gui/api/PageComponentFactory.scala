package com.openaf.browser.gui.api

trait PageComponentFactory {
  private[api] var application:OpenAFApplication = _
  final def pageComponent(context:BrowserContext):PageComponent = {
    val newPageComponent = pageComponent
    newPageComponent.initialise(context, application)
    newPageComponent
  }
  def pageComponent:PageComponent
}