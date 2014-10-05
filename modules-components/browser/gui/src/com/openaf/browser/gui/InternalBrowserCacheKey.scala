package com.openaf.browser.gui

import com.openaf.browser.gui.api.{SimpleBrowserCacheKeyWithDefault, OpenAFApplication, BrowserCacheKey}
import javafx.collections.{FXCollections, ObservableList}

object InternalBrowserCacheKey {
  // TODO - this should be specified so the applications comes out in order. Then we wouldn't have to order them after
  // TODO - straight after getting them out. Also, the order on the applications should take into account the name of
  // TODO - the application class when the order is the same. The application object should provide the comparator.
  val ApplicationsKey = {
    val key = BrowserCacheKey[ObservableList[OpenAFApplication]]("Browser Applications")
    new SimpleBrowserCacheKeyWithDefault(key, FXCollections.observableArrayList[OpenAFApplication])
  }
}
