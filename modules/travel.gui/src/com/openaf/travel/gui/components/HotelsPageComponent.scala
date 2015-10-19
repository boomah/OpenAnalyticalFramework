package com.openaf.travel.gui.components

import com.openaf.browser.gui.api.PageComponentFactory
import com.openaf.table.gui.api.TablePageComponent
import com.openaf.travel.api.{HotelsPage, HotelsPageData}

class HotelsPageComponent extends TablePageComponent {
  type P = HotelsPage
  type PD = HotelsPageData

  def nameId = "hotelsName"

  override def defaultRenderers = Map.empty
}

object HotelsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new HotelsPageComponent
}