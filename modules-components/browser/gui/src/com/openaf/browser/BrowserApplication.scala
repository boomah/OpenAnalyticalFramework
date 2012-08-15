package com.openaf.browser

trait BrowserApplication {
  def applicationName:String
  def homePageButtons(context:PageContext):List[BrowserApplicationButton] = Nil
}
