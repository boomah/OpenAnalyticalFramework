package com.openaf.rmiserverconnector

import org.osgi.framework._
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import com.openaf.properties.api.PropertiesService
import com.openaf.rmi.server.RMIServer
import com.openaf.osgi.OSGIUtils

class RmiServerConnectorBundleActivator extends BundleActivator {
  private var server:RMIServer = _

  def start(context:BundleContext) {
    println("Starting RMI Server (waiting for port)...")
    val propertiesServiceTracker = new ServiceTracker(context, classOf[PropertiesService], new ServiceTrackerCustomizer[PropertiesService,String] {
      def addingService(serviceReference:ServiceReference[PropertiesService]) = {
        val propertiesService = context.getService(serviceReference)
        val servicesPort = propertiesService.servicesPort
        println("Starting RMI Server (opening port %s).......".format(servicesPort))
        server = new RMIServer(servicesPort)
        server.start()
        ""
      }
      def modifiedService(serviceReference:ServiceReference[PropertiesService], string:String) {}
      def removedService(serviceReference:ServiceReference[PropertiesService], string:String) {}
    })
    propertiesServiceTracker.open()
    propertiesServiceTracker.waitForService(0)

    val filterString = "(" + OSGIUtils.ExportService + "=true)"
    val filter = context.createFilter(filterString)
    new ServiceTracker(context, filter, new ServiceTrackerCustomizer[AnyRef,String] {
      def addingService(serviceReference:ServiceReference[AnyRef]) = {
        addServiceToServer(serviceReference, context)
        ""
      }
      def modifiedService(serviceReference:ServiceReference[AnyRef], string:String) {}
      def removedService(serviceReference:ServiceReference[AnyRef], string:String) {}
    }).open()
    val currentExportedServices = context.getAllServiceReferences(null, filterString)
    currentExportedServices.foreach(ref => {
      addServiceToServer(ref, context)
    })
  }

  def stop(context:BundleContext) {
    println("Stopping RMI Server")
    Option(server).foreach(_.stop())
  }

  private def serviceNames(serviceReference:ServiceReference[_]) = {
    serviceReference.getProperty(Constants.OBJECTCLASS).asInstanceOf[Array[String]].toList
  }

  private def addServiceToServer(serviceReference:ServiceReference[_], context:BundleContext) {
    val firstServiceName = serviceNames(serviceReference).head
    val serviceAPI = Class.forName(firstServiceName)
    val service = context.getService(serviceReference).asInstanceOf[AnyRef]
    server.addService(serviceAPI, service)
  }
}
