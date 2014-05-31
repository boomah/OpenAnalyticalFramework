package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.PageFactory
import javafx.beans.binding.StringBinding

trait OpenAFApplication {
  def applicationNameBinding(context:PageContext):StringBinding
  def applicationButtons(context:PageContext):List[BrowserActionButton] = Nil
  def utilButtons(context:PageContext):List[BrowserActionButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
  def styleSheets:List[String] = Nil
}

case class BrowserActionButton(name:String, pageFactory:PageFactory)
