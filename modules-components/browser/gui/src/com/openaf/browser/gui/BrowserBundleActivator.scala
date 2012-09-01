package com.openaf.browser.gui

import org.osgi.framework.{ServiceReference, BundleContext}
import com.openaf.osgi.OpenAFBundleActivator
import com.google.common.eventbus.EventBus
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}

class BrowserBundleActivator extends OpenAFBundleActivator {
  def start(context:BundleContext) {
    run({
      javafx.application.Application.launch(classOf[BrowserStageManager], context.getProperty("openAF.instanceName"))
    }, "Browser Bundle Activator starter")
    run({
      val eventBus = new EventBus
      val browserStageManager = BrowserStageManager.waitForBrowserStageManager
      eventBus.register(browserStageManager)
      new ServiceTracker(context, classOf[BrowserApplication], new ServiceTrackerCustomizer[BrowserApplication,BrowserApplication]{
        def addingService(serviceReference:ServiceReference[BrowserApplication]) = {
          val browserApplication = context.getService(serviceReference)
          eventBus.post(browserApplication)
          browserApplication
        }
        def modifiedService(serviceReference:ServiceReference[BrowserApplication], browserApplication:BrowserApplication) {}
        def removedService(serviceReference:ServiceReference[BrowserApplication], browserApplication:BrowserApplication) {}
      }).open()
    })
  }
  def stop(context:BundleContext) {
    println("Can't restart the browser at the moment - will have to exit")
    Thread.sleep(3000)
    System.exit(1)
  }
}
