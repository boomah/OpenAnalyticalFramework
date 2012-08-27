package com.openaf.viewer

import com.openaf.browser.{PageData, PageFactory, Page}

case object ViewPage extends Page {
  def name = "View"
  def image = null
  def build = PageData.NoPageData
}

object ViewerPageFactory extends PageFactory {
  def page = ViewPage
}