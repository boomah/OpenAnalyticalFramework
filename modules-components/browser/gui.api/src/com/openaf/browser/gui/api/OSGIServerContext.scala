package com.openaf.browser.gui.api

import org.osgi.framework.BundleContext
import org.osgi.util.tracker.ServiceTracker
import com.openaf.pagemanager.api.ServerContext

class OSGIServerContext(context:BundleContext) extends ServerContext {
  def facility[T](klass:Class[T]) = {
    val serviceTracker = new ServiceTracker(context, klass, null)
    serviceTracker.open()
    val services = serviceTracker.getServices
    services(0).asInstanceOf[T]
  }
}
