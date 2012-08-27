package com.openaf.browser.components

import com.openaf.browser.PageData
import javafx.scene.Parent

trait PageComponent extends Parent {
  private var currentPageData:PageData = _
  final def pageData = currentPageData
  final def pageData_=(pd:PageData) {
    currentPageData = pd
    initialise(pageData)
  }
  def initialise(pageData:PageData)
}