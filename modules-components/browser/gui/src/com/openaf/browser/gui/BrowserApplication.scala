package com.openaf.browser.gui

import components.PageComponentFactory

trait BrowserApplication {
  def applicationName:String
  def browserApplicationButtons(context:PageContext):List[BrowserActionButton] = Nil
  def browserUtilsButtons(context:PageContext):List[BrowserActionButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
}

case class BrowserActionButton(name:String, pageFactory:PageFactory)
