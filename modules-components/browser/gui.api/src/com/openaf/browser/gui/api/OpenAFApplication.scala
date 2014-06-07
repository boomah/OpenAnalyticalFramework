package com.openaf.browser.gui.api

import com.openaf.pagemanager.api.PageFactory

trait OpenAFApplication {
  def resourceLocation:String = {
    val className = getClass.getName
    val Module = """com\.openaf\.(.+)\.gui\..*""".r
    if (className.matches(Module.toString)) {
      val Module(module) = className
      "com.openaf.%s.gui.resources.%s".format(module, module)
    } else {
      ""
    }
  }
  def applicationButtons(context:PageContext):List[BrowserActionButton] = Nil
  def utilButtons(context:PageContext):List[BrowserActionButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
  def styleSheets:List[String] = Nil
}

case class BrowserActionButton(name:String, pageFactory:PageFactory)
