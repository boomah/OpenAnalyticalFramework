package com.openaf.cache

import com.google.common.cache.CacheBuilder
import java.util.concurrent.Callable
import scala.collection.JavaConversions._
import com.google.common.eventbus.EventBus

object CacheFactory {
  private val eventBus = new EventBus
  private val cacheCache = new Cache(soft = false)
  def cache(cacheName:String, soft:Boolean=false) = {
    var cacheAdded = false
    val cache = cacheCache.memoize(cacheName) {
      cacheAdded = true
      new Cache(soft)
    }
    if (cacheAdded) {
      eventBus.post(CacheChanged())
    }
    cache
  }
  def allCacheNames = cacheCache.keys[String]
  def clearCache(cacheName:String) {cache(cacheName).clear()}
  def clearAllCaches() {cacheCache.keys.foreach(clearCache)}
  def registerListener(listener:AnyRef) {eventBus.register(listener)}
}

class Cache(soft:Boolean) {
  private val cache = {
    val builder = {
      val newBuilder = CacheBuilder.newBuilder
      if (soft) newBuilder.softValues else newBuilder
    }
    builder.build[AnyRef,AnyRef]
  }

  def memoize[K,V](key:K)(f: =>V) = {
    cache.get(key.asInstanceOf[AnyRef], new Callable[AnyRef] {
      def call = f.asInstanceOf[AnyRef]
    }).asInstanceOf[V]
  }

  def invalidate[K](key:K) {cache.invalidate(key)}
  def keys[K]:Set[K] = cache.asMap.keySet.toSet.asInstanceOf[Set[K]]
  def clear() {cache.invalidateAll()}
}

case class CacheChanged()