package com.openaf.browser.gui.components

import com.openaf.browser.gui.api.PageComponent
import com.openaf.browser.gui.BrowserApplication
import com.openaf.browser.gui.utils.BrowserUtils

trait BrowserPageComponent extends PageComponent {
  override protected def resourceLocation = BrowserApplication.resourceLocation
  override def image = Some(BrowserUtils.icon("16x16_home.png"))
}
