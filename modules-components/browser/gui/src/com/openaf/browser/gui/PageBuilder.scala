package com.openaf.browser.gui

import java.util.concurrent.{ThreadFactory, Executors}
import com.openaf.cache.CacheFactory
import com.openaf.pagemanager.api._
import com.openaf.gui.utils.GuiUtils

class PageBuilder(serverContext:ServerContext) {
  private val pageDataCache = CacheFactory.cache("browser.pageData", soft = true)
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
          val pageData = pageDataCache.memoize(page) {page.pageDataFacility(serverContext).pageData(page)}
          SuccessPageResponse(pageData)
        } catch {
          case e:Exception => ProblemPageResponse(e)
        }
        GuiUtils.runLater(withResult(pageResponse))
      }
    })
  }
}

sealed trait PageResponse
case class SuccessPageResponse(pageData:PageData) extends PageResponse
case class ProblemPageResponse(exception:Exception) extends PageResponse
