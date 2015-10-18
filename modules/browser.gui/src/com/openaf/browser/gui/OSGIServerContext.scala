package com.openaf.browser.gui

import org.osgi.framework.BundleContext
import org.osgi.util.tracker.ServiceTracker
import com.openaf.pagemanager.api.ServerContext

class OSGIServerContext(context:BundleContext) extends ServerContext {
  def facilities[T](klass:Class[T]):List[T] = {
    val serviceTracker = new ServiceTracker(context, klass, null)
    serviceTracker.open()
    val services = serviceTracker.getServices
    services.map(_.asInstanceOf[T]).toList
  }
}
