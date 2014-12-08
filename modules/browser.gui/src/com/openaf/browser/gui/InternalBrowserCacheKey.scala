package com.openaf.browser.gui

import com.openaf.browser.gui.api.{BrowserCacheKeyWithDefaultAndSimpleMap, OpenAFApplication, BrowserCacheKey}
import javafx.collections.{FXCollections, ObservableList}
import java.util.Comparator

object InternalBrowserCacheKey {
  val ApplicationsKey = new BrowserCacheKeyWithDefaultAndSimpleMap[ObservableList[OpenAFApplication]] {
    val comparator = new Comparator[OpenAFApplication] {
      def compare(left:OpenAFApplication, right:OpenAFApplication) = {
        val result = Integer.compare(left.order, right.order)
        if (result == 0) {
          left.getClass.getSimpleName.compareTo(right.getClass.getSimpleName)
        } else {
          result
        }
      }
    }
    val browserCacheKey = BrowserCacheKey[ObservableList[OpenAFApplication]]("Browser Applications")
    val default = FXCollections.observableArrayList[OpenAFApplication]
    def map(value:ObservableList[OpenAFApplication]) = {
      FXCollections.sort(value, comparator)
      value
    }
  }
}
