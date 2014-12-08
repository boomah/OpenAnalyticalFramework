package com.openaf.rmiserverconnector

import org.osgi.framework._
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import com.openaf.properties.api.PropertiesService
import com.openaf.rmi.server.RMIServer
import com.openaf.osgi.{OpenAFBundleActivator, OSGIUtils}

class RmiServerConnectorBundleActivator extends OpenAFBundleActivator {
  private var server:RMIServer = _

  protected def startUp(context:BundleContext) {
    println("Starting RMI Server (waiting for port)...")
    val servicesPort = waitForService[PropertiesService](context).servicesPort
    println(s"Starting RMI Server (opening port $servicesPort).......")
    server = new RMIServer(servicesPort)
    server.start()

    val filterString = "(" + OSGIUtils.ExportService + "=true)"
    val filter = context.createFilter(filterString)

    new ServiceTracker(context, filter, new ServiceTrackerCustomizer[Any,Any] {
      def addingService(serviceReference:ServiceReference[Any]) = {
        addServiceToServer(serviceReference, context)
        ""
      }
      def modifiedService(serviceReference:ServiceReference[Any], any:Any) {}
      def removedService(serviceReference:ServiceReference[Any], any:Any) {
        removeServiceFromServer(serviceReference, context)
      }
    }).open()
  }

  def stop(context:BundleContext) {
    println("Stopping RMI Server")
    Option(server).foreach(_.stop())
  }

  private def serviceNames(serviceReference:ServiceReference[_]) = {
    serviceReference.getProperty(Constants.OBJECTCLASS).asInstanceOf[Array[String]].toList
  }

  private def addServiceToServer(serviceReference:ServiceReference[_], context:BundleContext) {
    val service = context.getService(serviceReference).asInstanceOf[AnyRef]
    val addedServiceNames = serviceNames(serviceReference)
    addedServiceNames.foreach(serviceName => {server.addService(serviceName, service)})
  }

  private def removeServiceFromServer(serviceReference:ServiceReference[_], context:BundleContext) {
    val removedServiceNames = serviceNames(serviceReference)
    removedServiceNames.foreach(serviceName => {server.removeService(serviceName)})
  }
}