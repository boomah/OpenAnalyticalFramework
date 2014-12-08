package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.Page

trait BrowserContext {
  def cache:BrowserCache
  def goToPage(page:Page)
  def nameChanged()
  def descriptionChanged()
  def imageChanged()
}
