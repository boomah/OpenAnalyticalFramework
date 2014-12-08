package com.openaf.testconsumer.gui

import com.openaf.test.api.TestService
import org.osgi.framework.BundleContext
import com.openaf.osgi.OpenAFBundleActivator


class TestConsumerBundleActivator extends OpenAFBundleActivator {
  protected def startUp(context:BundleContext) {
    println("")
    println("-- Starting Testconsumer --")
    println("")
    val service = waitForService[TestService](context)
    val message = service.message
    println("****")
    println("A service has been added : " + message)
    println("Call something else " + service.message2("Nick"))
    println("Call the 3rd service : " + service.message3(true, "Rosie"))
    println("****")
  }

  def stop(context:BundleContext) {
    println("")
    println("-- Stopping Testconsumer --")
    println("")
  }
}
