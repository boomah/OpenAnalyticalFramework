package com.openaf.browser.gui.components

import com.openaf.browser.gui.api.PageComponent
import com.openaf.browser.gui.BrowserApplication

trait BrowserPageComponent extends PageComponent {
  override protected def resourceLocation = BrowserApplication.resourceLocation
}
