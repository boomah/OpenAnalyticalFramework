package com.openaf.browser

import org.osgi.framework.{BundleContext, BundleActivator}

class BrowserBundleActivator extends BundleActivator {
  def start(context:BundleContext) {javafx.application.Application.launch(classOf[BrowserStageManager], System.getProperty("instanceName"))}
  def stop(context:BundleContext) {
    println("Can't restart the browser at the moment - will have to exit")
    Thread.sleep(3000)
    System.exit(1)
  }
}
