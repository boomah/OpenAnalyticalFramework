package com.openaf.rmiclientconnector

import org.osgi.framework._
import com.openaf.rmi.client.RMIClient
import com.openaf.rmi.common.ServicesListing

class RmiClientConnectorBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    val hostName = context.getProperty("openAF.hostName")
    val servicesPort = context.getProperty("openAF.servicesPort").toInt
    println("Starting RMI Client (connecting to %s:%s)...".format(hostName, servicesPort))
    val client = new RMIClient(hostName, servicesPort)
    client.connectBlocking()
    val servicesListingClass = classOf[ServicesListing]
    val servicesListing = client.proxy(servicesListingClass)
    val services = servicesListing.services.filterNot(_ == servicesListingClass.getName)
    val packagesToBundleMap = packagesToBundle(context)
    services.foreach(serviceName => {
      loadClass(serviceName, packagesToBundleMap) match {
        case Some(serviceClass:Class[_]) => {
          val service = client.proxy(serviceClass.asInstanceOf[Class[AnyRef]])
          context.registerService(serviceClass, service, null)
        }
        case _ =>
      }
    })
    println("Services : " + services)
  }

  def stop(context:BundleContext) {}

  private def packagesToBundle(context:BundleContext) = {
    val bundles = context.getBundles
    bundles.map(bundle => {
      val exportPackageString = bundle.getHeaders.get("Export-Package")
      val withoutUses = exportPackageString.replaceAll("""uses:=\"(\\.|[^\"])*\"""", "")
      (withoutUses -> bundle)
    }).toMap
  }

  private def loadClass(className:String, packagesToBundle:Map[String,Bundle]) = {
    val classPackage = className.substring(0, className.lastIndexOf('.'))
    packagesToBundle.find{case (packageString, bundle) => packageString.contains(classPackage)}.map{case ((_,bundle)) => {
      bundle.loadClass(className)
    }}
  }
}
