package com.openaf.travel.api

import com.openaf.pagemanager.api.{ServerContext, Page}

case class HotelsPage() extends Page {
  def name = "Hotels"
  def pageDataFacility(serverContext:ServerContext) = null
}
