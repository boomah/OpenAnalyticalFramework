package com.openaf.browser.gui.components

import com.openaf.browser.gui.PageData
import javafx.scene.layout.Region

trait PageComponent extends Region {
  private var currentPageData:PageData = _
  final def pageData = currentPageData
  final def pageData_=(pd:PageData) {
    currentPageData = pd
    initialise(pageData)
  }
  def initialise(pageData:PageData)
}