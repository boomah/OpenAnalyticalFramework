package com.openaf.browser.gui

import com.openaf.browser.gui.api.{SimpleBrowserCacheKeyWithDefault, OpenAFApplication, BrowserCacheKey}
import javafx.collections.{FXCollections, ObservableList}

object InternalBrowserCacheKey {
  val ApplicationsKey = {
    val key = BrowserCacheKey[ObservableList[OpenAFApplication]]("Browser Applications")
    new SimpleBrowserCacheKeyWithDefault(key, FXCollections.observableArrayList[OpenAFApplication])
  }
}
