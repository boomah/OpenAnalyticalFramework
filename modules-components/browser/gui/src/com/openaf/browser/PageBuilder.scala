package com.openaf.browser

import com.google.common.cache.CacheBuilder
import java.util.concurrent.{ThreadFactory, Executors, Callable}
import utils.BrowserUtils

class PageBuilder {
  private val pageDataCache = CacheBuilder.newBuilder.softValues.build[Page,PageData]
  private val threadPool = Executors.newFixedThreadPool(10, new ThreadFactory {
    def newThread(r:Runnable) = {
      val thread = new Thread(r, "Page Builder")
      thread.setPriority(Thread.currentThread.getPriority - 1)
      thread
    }
  })

  def build(page:Page, withResult:(PageResponse) => Unit) = {
    threadPool.submit(new Runnable {
      def run() {
        val pageResponse = try {
          val pageData = pageDataCache.get(page, new Callable[PageData] {
            def call = page.build
          })
          SuccessPageResponse(pageData)
        } catch {
          case t:Throwable => ProblemPageResponse(t)
        }
        BrowserUtils.runLater(withResult(pageResponse))
      }
    })
  }

  def clearPageDataCache() {
    pageDataCache.asMap.clear()
  }
}

sealed trait PageResponse
case class SuccessPageResponse(pageData:PageData) extends PageResponse
case class ProblemPageResponse(throwable:Throwable) extends PageResponse