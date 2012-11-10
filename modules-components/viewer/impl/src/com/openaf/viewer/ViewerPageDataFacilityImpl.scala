package com.openaf.viewer

import api.{ViewerPageDataFacility, ViewerPageData}
import com.openaf.pagemanager.api.Page

class ViewerPageDataFacilityImpl extends ViewerPageDataFacility {
  private var counter = 0
  private def text = {
    Thread.sleep(1000)
    counter += 1
    "This is text from ViewerFacility " + counter
  }
  def pageData(page:Page) = ViewerPageData(text)
}
