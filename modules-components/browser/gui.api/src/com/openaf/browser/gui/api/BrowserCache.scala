package com.openaf.browser.gui.api

import collection.mutable
import javafx.collections.{FXCollections, ObservableList}
import com.openaf.browser.gui.api.utils.BrowserUtils
import javafx.beans.property.SimpleObjectProperty
import java.util.Locale

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
class SimpleBrowserCacheKeyWithDefault[T](browserCacheKey0:BrowserCacheKey[T], val default:T) extends BrowserCacheKeyWithDefault(browserCacheKey0)

object BrowserCacheKey {
  val ApplicationsKey = {
    val key = BrowserCacheKey[ObservableList[OpenAFApplication]]("Browser Applications")
    new SimpleBrowserCacheKeyWithDefault(key, FXCollections.observableArrayList[OpenAFApplication])
  }
  val LocaleKey = {
    val key = BrowserCacheKey[SimpleObjectProperty[Locale]]("Locale")
    new SimpleBrowserCacheKeyWithDefault(key, new SimpleObjectProperty[Locale](Locale.getDefault))
  }
}