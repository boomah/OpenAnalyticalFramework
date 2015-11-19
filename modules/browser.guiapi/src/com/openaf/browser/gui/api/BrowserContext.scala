package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.{PageData, Page}

trait BrowserContext {
  def cache:BrowserCache
  def goToPage(page:Page):Unit = goToPage(page, None)

  /**
   * Go to the specified page. If pageData is provided it means that the component has been able to generate it so that
   * a server call is not required.
   */
  def goToPage(page:Page, pageDataOption:Option[PageData]):Unit
  def nameChanged():Unit
  def descriptionChanged():Unit
  def imageChanged():Unit
}
