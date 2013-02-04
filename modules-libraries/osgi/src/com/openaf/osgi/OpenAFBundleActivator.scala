package com.openaf.osgi

import org.osgi.framework.{BundleContext, BundleActivator}
import org.osgi.util.tracker.ServiceTracker
import reflect._

abstract class OpenAFBundleActivator extends BundleActivator {
  protected def run(function: => Unit) {
    new Thread(new Runnable {
      def run() {function}
    }).start()
  }
  protected def run(function: => Unit, threadName:String) {
    new Thread(new Runnable {
      def run() {function}
    }, threadName).start()
  }
  final protected def waitForService[T:ClassTag](context:BundleContext):T = {
    val serviceTracker = new ServiceTracker(context, classTag[T].runtimeClass, null)
    serviceTracker.open()
    serviceTracker.waitForService(0).asInstanceOf[T]
  }

  final def start(context:BundleContext) {run(startUp(context), s"Starting Activator ${getClass.getName}")}

  protected def startUp(context:BundleContext)
}
