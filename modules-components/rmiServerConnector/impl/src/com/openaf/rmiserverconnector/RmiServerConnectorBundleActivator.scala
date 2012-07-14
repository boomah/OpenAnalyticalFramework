package com.openaf.rmiserverconnector

import org.osgi.framework.{ServiceReference, BundleContext, BundleActivator}
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import com.openaf.properties.api.PropertiesService
import com.openaf.rmi.server.RMIServer

class RmiServerConnectorBundleActivator extends BundleActivator {
  private var server:RMIServer = _

  def start(context:BundleContext) {
    new ServiceTracker(context, classOf[PropertiesService], new ServiceTrackerCustomizer[PropertiesService,String] {
      def addingService(serviceReference:ServiceReference[PropertiesService]) = {
        val propertiesService = context.getService(serviceReference)
        val servicesPort = propertiesService.servicesPort
        println("Starting RMI Server (opening port %s)...".format(servicesPort))
        server = new RMIServer(servicesPort)
        server.start()
        ""
      }
      def modifiedService(serviceReference:ServiceReference[PropertiesService], string:String) {}
      def removedService(serviceReference:ServiceReference[PropertiesService], string:String) {}
    }).open()
  }

  def stop(context:BundleContext) {
    Option(server).foreach(_.stop())
  }
}
