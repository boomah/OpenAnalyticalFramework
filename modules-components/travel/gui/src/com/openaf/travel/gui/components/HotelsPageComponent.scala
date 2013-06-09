package com.openaf.travel.gui.components

import com.openaf.browser.gui.api.PageComponentFactory
import com.openaf.table.gui.api.TablePageComponent

class HotelsPageComponent extends TablePageComponent {
  def name = "Hotels"
}

object HotelsPageComponentFactory extends PageComponentFactory {
  def pageComponent = new HotelsPageComponent
}