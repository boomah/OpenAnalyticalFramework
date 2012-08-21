package com.openaf.osgi

import org.osgi.framework.BundleActivator

abstract class OpenAFBundleActivator extends BundleActivator {
  def run(function: => Unit) {
    new Thread(new Runnable {
      def run() {function}
    }).start()
  }
  def run(function: => Unit, threadName:String) {
    new Thread(new Runnable {
      def run() {function}
    }, threadName).start()
  }
}
