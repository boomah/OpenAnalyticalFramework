package com.openaf.browser.gui.api

import collection.mutable
import javafx.application.Platform
import javafx.beans.property.{Property, SimpleObjectProperty}
import java.util.Locale

class BrowserCache {
  private val cache = new mutable.HashMap[BrowserCacheKey[_],Any]
  def apply[T](browserCacheKey:BrowserCacheKey[T]) = {
    checkFXThread()
    cache(browserCacheKey).asInstanceOf[T]
  }
  def apply[T](browserCacheKeyWithDefault:BrowserCacheKeyWithDefault[T]) = {
    checkFXThread()
    cache.get(browserCacheKeyWithDefault.browserCacheKey) match {
      case Some(value) => value.asInstanceOf[T]
      case _ => browserCacheKeyWithDefault.default
    }
  }
  def apply[T](browserCacheKeyWithDefaultAndSimpleMap:BrowserCacheKeyWithDefaultAndSimpleMap[T]) = {
    checkFXThread()
    def map(value:T) = browserCacheKeyWithDefaultAndSimpleMap.map(value)
    cache.get(browserCacheKeyWithDefaultAndSimpleMap.browserCacheKey) match {
      case Some(value) => map(value.asInstanceOf[T])
      case _ => map(browserCacheKeyWithDefaultAndSimpleMap.default)
    }
  }
  def update[T](browserCacheKey:BrowserCacheKey[T], value:T) = {
    checkFXThread()
    cache.put(browserCacheKey, value)
    browserCacheKey
  }
  private def checkFXThread() {require(Platform.isFxApplicationThread, "This must be called on the FX Application Thread")}
}

case class BrowserCacheKey[T](description:String)
trait BrowserCacheKeyWithDefault[T] {
  val browserCacheKey:BrowserCacheKey[T]
  def default:T
}
trait BrowserCacheKeyWithDefaultAndSimpleMap[T] extends BrowserCacheKeyWithDefault[T] {
  def map(value:T):T
}
class SimpleBrowserCacheKeyWithDefault[T](val browserCacheKey:BrowserCacheKey[T], val default:T) extends BrowserCacheKeyWithDefault[T]

object BrowserCacheKey {
  val LocaleKey = {
    val key = BrowserCacheKey[Property[Locale]]("Locale")
    new SimpleBrowserCacheKeyWithDefault(key, new SimpleObjectProperty[Locale](Locale.getDefault))
  }
}