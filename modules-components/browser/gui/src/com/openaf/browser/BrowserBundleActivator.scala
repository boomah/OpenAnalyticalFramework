package com.openaf.browser

import org.osgi.framework.{BundleContext, BundleActivator}

class BrowserBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("^^^^Starting Browser^^^^")
    javafx.application.Application.launch(classOf[FrameManager], "array")
    println("Created JavaFX Component")
  }

  def stop(context:BundleContext) {}
}
