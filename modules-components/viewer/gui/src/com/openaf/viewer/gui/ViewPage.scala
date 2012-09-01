package com.openaf.viewer.gui

import com.openaf.browser.gui.{ServerContext, PageData, PageFactory, Page}
import com.openaf.viewer.api.ViewerFacility

case class ViewPage(number:Int) extends Page {
  def name = "View"
  def image = null
  type SC = ViewerFacility
  def createServerContext(serverContext:ServerContext) = serverContext.facility(classOf[ViewerFacility])
  def build(viewerFacility:ViewerFacility) = ViewPageData(viewerFacility.text)
}

object ViewerPageFactory extends PageFactory {
  def page = ViewPage(0)
}

case class ViewPageData(text:String) extends PageData