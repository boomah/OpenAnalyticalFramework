package com.openaf.travel

import api.TravelPageDataFacility
import com.openaf.pagemanager.api.{NoPageData, Page}

class TravelPageDataFacilityImpl extends TravelPageDataFacility {
  def pageData(page:Page) = NoPageData
}
