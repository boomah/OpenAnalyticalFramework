package com.openaf.rmi.common

import java.util.concurrent.ThreadFactory

case class RMINamedThreadFactory(name:String) extends ThreadFactory {
  def newThread(runnable:Runnable) = {
    new Thread(runnable, name)
  }
}
