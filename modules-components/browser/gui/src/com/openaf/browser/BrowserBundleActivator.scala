package com.openaf.browser

import org.osgi.framework.BundleContext
import com.openaf.osgi.OpenAFBundleActivator

class BrowserBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    run({
      javafx.application.Application.launch(classOf[BrowserStageManager], context.getProperty("openAF.instanceName"))
    }, "Browser Bundle Activator starter")
    run({
      val browserCommunicator = BrowserStageManager.waitForBrowserCommunicator
      browserCommunicator.sayHi()
    })
  }
  def stop(context:BundleContext) {
    println("Can't restart the browser at the moment - will have to exit")
    Thread.sleep(3000)
    System.exit(1)
  }
}
