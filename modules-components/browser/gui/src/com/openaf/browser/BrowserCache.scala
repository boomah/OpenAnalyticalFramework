package com.openaf.browser

import collection.mutable
import utils.BrowserUtils

class BrowserCache {
  private val cache = new mutable.HashMap[BrowserCacheKey[_],Any]
  def apply[T](browserCacheKey:BrowserCacheKey[T]) = {
    BrowserUtils.checkFXThread()
    cache(browserCacheKey).asInstanceOf[T]
  }
  def update[T](browserCacheKey:BrowserCacheKey[T], value:T) = {
    BrowserUtils.checkFXThread()
    cache.put(browserCacheKey, value)
    browserCacheKey
  }
}

case class BrowserCacheKey[T](description:String)

object BrowserCacheKey {
  val BrowserApplicationsKey = BrowserCacheKey[List[BrowserApplication]]("Browser Applications")
}