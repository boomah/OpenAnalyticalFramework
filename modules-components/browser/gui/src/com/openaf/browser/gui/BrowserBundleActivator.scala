package com.openaf.browser.gui

import org.osgi.framework.{ServiceReference, BundleContext}
import com.openaf.osgi.OpenAFBundleActivator
import com.google.common.eventbus.EventBus
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import com.openaf.browser.gui.utils.BrowserUtils
import com.openaf.browser.gui.api.OpenAFApplication

class BrowserBundleActivator extends OpenAFBundleActivator {
  protected def startUp(context:BundleContext) {
    // This is a blocking call so run it on another thread
    run(javafx.application.Application.launch(classOf[BrowserStageManager], context.getProperty("com.openAF.instanceName")))

    context.registerService(classOf[OpenAFApplication], BrowserApplication, null)
    val eventBus = new EventBus
    val browserStageManager = BrowserStageManager.waitForBrowserStageManager
    eventBus.register(browserStageManager)
    new ServiceTracker(context, classOf[OpenAFApplication], new ServiceTrackerCustomizer[OpenAFApplication,OpenAFApplication]{
      def addingService(serviceReference:ServiceReference[OpenAFApplication]) = {
        val openAFApplication = context.getService(serviceReference)
        eventBus.post(OpenAFApplicationAdded(openAFApplication))
        openAFApplication
      }
      def modifiedService(serviceReference:ServiceReference[OpenAFApplication], openAFApplication:OpenAFApplication) {}
      def removedService(serviceReference:ServiceReference[OpenAFApplication], openAFApplication:OpenAFApplication) {
        println("!! OpenAFApplication Removed")
        eventBus.post(OpenAFApplicationRemoved(openAFApplication))
      }
    }).open()
    BrowserUtils.runLater({
      val pageBuilder = new PageBuilder(new OSGIServerContext(context))
      browserStageManager.start(pageBuilder)
    })
  }

  def stop(context:BundleContext) {
    println("Can't restart the browser at the moment - will have to exit")
//    Thread.sleep(3000)
    System.exit(1)
  }
}
