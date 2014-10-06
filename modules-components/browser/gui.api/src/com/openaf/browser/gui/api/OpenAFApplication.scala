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
  def applicationButtons(context:BrowserContext):List[BrowserActionButton] = Nil
  def utilButtons(context:BrowserContext):List[BrowserActionButton] = Nil
  def componentFactoryMap:Map[String,PageComponentFactory] = Map.empty
  final def componentFactoryMapWithInitialisation = {
    val newComponentFactoryMap = componentFactoryMap
    newComponentFactoryMap.values.foreach(_.application = this)
    newComponentFactoryMap
  }
  def styleSheets:List[String] = Nil

  /**
   * Relative order that the applications. If the order matches, the simple class name of the application is used.
   */
  def order:Int = 0
}

case class BrowserActionButton(name:String, pageFactory:PageFactory)
