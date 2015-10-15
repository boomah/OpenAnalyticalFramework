package com.openaf.viewer.api

import com.openaf.pagemanager.api._

case class ViewerPage(number:Int) extends Page {
  def pageDataFacility(serverContext:ServerContext) = serverContext.facility(classOf[ViewerPageDataFacility])
}

case class ViewerPageData(text:String) extends PageData