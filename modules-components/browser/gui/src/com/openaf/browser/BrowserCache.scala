package com.openaf.browser

import collection.mutable
import utils.BrowserUtils
import javafx.collections.{FXCollections, ObservableList}

class BrowserCache {
  private val cache = new mutable.HashMap[BrowserCacheKey[_],Any]
  def apply[T](browserCacheKey:BrowserCacheKey[T]) = {
    BrowserUtils.checkFXThread()
    cache(browserCacheKey).asInstanceOf[T]
  }
  def apply[T](browserCacheKeyWithDefault:BrowserCacheKeyWithDefault[T]) = {
    BrowserUtils.checkFXThread()
    cache.get(browserCacheKeyWithDefault.browserCacheKey) match {
      case Some(value) => value.asInstanceOf[T]
      case _ => browserCacheKeyWithDefault.default
    }
  }
  def update[T](browserCacheKey:BrowserCacheKey[T], value:T) = {
    BrowserUtils.checkFXThread()
    cache.put(browserCacheKey, value)
    browserCacheKey
  }
}

case class BrowserCacheKey[T](description:String)
abstract class BrowserCacheKeyWithDefault[T](val browserCacheKey:BrowserCacheKey[T]) {
  def default:T
}

object BrowserCacheKey {
  val BrowserApplicationsKeyWithDefault = {
    val key = BrowserCacheKey[ObservableList[BrowserApplication]]("Browser Applications")
    BrowserCacheKeyWithDefault.create(key, FXCollections.observableArrayList[BrowserApplication])
  }
}

object BrowserCacheKeyWithDefault {
  def create[T](browserCacheKey:BrowserCacheKey[T], default0:T) = {
    new BrowserCacheKeyWithDefault[T](browserCacheKey) {
      def default = default0
    }
  }
}