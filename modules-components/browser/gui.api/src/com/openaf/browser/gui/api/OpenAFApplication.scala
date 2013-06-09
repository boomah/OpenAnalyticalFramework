package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.PageFactory

trait OpenAFApplication {
  def applicationName:String
  def applicationButtons(context:PageContext):List[BrowserActionButton] = Nil
  def utilButtons(context:PageContext):List[BrowserActionButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
  def styleSheets:List[String] = Nil
}

case class BrowserActionButton(name:String, pageFactory:PageFactory)
