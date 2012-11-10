package com.openaf.viewer.api

import com.openaf.pagemanager.api._

case class ViewerPage(number:Int) extends Page {
  def name = "View"
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[ViewerPageDataFacility])
}

object ViewerPageFactory extends PageFactory {
  def page = ViewerPage(0)
}

case class ViewerPageData(text:String) extends PageData