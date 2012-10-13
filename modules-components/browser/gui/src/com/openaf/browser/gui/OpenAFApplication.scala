package com.openaf.browser.gui

import components.PageComponentFactory

trait OpenAFApplication {
  def applicationName:String
  def applicationButtons(context:PageContext):List[BrowserActionButton] = Nil
  def utilButtons(context:PageContext):List[BrowserActionButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
}

case class BrowserActionButton(name:String, pageFactory:PageFactory)
