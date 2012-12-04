package com.openaf.travel.gui.components

import com.openaf.browser.gui.components.{PageComponentFactory, PageComponent}
import com.openaf.travel.api.HotelsPage
import com.openaf.pagemanager.api.NoPageData
import com.openaf.browser.gui.PageContext

class HotelsPageComponent extends PageComponent {
  type P = HotelsPage
  type PD = NoPageData.type

  def name = "Hotels"
  def setup() {}
}

object HotelsPageComponentFactory extends PageComponentFactory {
  def pageComponent(pageContext:PageContext) = new HotelsPageComponent
}