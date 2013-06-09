package com.openaf.browser.gui.utils

sealed trait OS {
  def windows = true
}
case object OSX extends OS {
  override def windows = false
}
case object WindowsXP extends OS
case object Windows7 extends OS
case object WindowsUnknown extends OS
case object Linux extends OS {
  override def windows = false
}
