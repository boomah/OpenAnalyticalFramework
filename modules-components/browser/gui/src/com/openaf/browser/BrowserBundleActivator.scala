package com.openaf.browser

import org.osgi.framework.{BundleContext, BundleActivator}

class BrowserBundleActivator extends BundleActivator {
  def start(context:BundleContext) {javafx.application.Application.launch(classOf[FrameManager], "Add Properties")}
  def stop(context:BundleContext) {}
}
