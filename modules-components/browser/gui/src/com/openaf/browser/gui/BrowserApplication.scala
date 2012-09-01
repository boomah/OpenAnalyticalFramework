package com.openaf.browser.gui

import components.PageComponentFactory

trait BrowserApplication {
  def applicationName:String
  def browserApplicationButtons(context:PageContext):List[BrowserApplicationButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
}

case class BrowserApplicationButton(name:String, pageFactory:PageFactory)
