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
        println("A service has been added : " + message)
        println("Call something else " + service.message2("Nick"))
        println("Call the 3rd service : " + service.message3(true, "Rosie"))
        println("****")
        "Not sure what to return"
      }
      def modifiedService(serviceReference:ServiceReference[TestService], string:String) {
        println("----")
        println("A service has been Modified!!!")
        println("----")
      }
      def removedService(serviceReference:ServiceReference[TestService], string:String) {
        println("****")
        println("A service has been removed : " + string)
        println("****")
      }
    }).open()
  }
  def stop(context:BundleContext) {
    println("")
    println("-- Stopping Testconsumer --")
    println("")
  }
}
