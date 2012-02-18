package com.openaf.testconsumer

import com.openaf.test.api.TestService
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import org.osgi.framework.{ServiceReference, BundleContext, BundleActivator}


class TestConsumerBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("")
    println("-- Starting Testconsumer --")
    println("")

    new ServiceTracker(context, classOf[TestService], new ServiceTrackerCustomizer[TestService, String] {
      def addingService(serviceReference:ServiceReference[TestService]):String = {
        val service = context.getService(serviceReference)
        val message = service.message
        println("****")
        println("Got a message from TestService : " + message)
        println("****")
        "Not sure what to return"
      }
      def modifiedService(serviceReference:ServiceReference[TestService], string:String) {}
      def removedService(serviceReference:ServiceReference[TestService], string:String) {}
    }).open()
  }
  def stop(context:BundleContext) {}
}
