package com.openaf.viewer

import com.openaf.browser.{PageData, PageFactory, Page}

case object ViewPage extends Page {
  def name = "View"
  def image = null
  def build = ViewPageData("This is the viewer page component")
}

object ViewerPageFactory extends PageFactory {
  def page = ViewPage
}

case class ViewPageData(text:String) extends PageData