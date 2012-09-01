package com.openaf.browser.gui

trait ServerContext {
  def facility[T](klass:Class[T]):T
}