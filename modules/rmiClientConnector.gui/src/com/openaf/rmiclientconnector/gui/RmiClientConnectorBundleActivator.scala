package com.openaf.rmiclientconnector.gui

import org.osgi.framework._
import com.openaf.rmi.client.RMIClient
import com.openaf.rmi.common.ServicesListing
import com.openaf.osgi.OpenAFBundleActivator

class RmiClientConnectorBundleActivator extends OpenAFBundleActivator {
  def startUp(context:BundleContext) {
    val hostName = context.getProperty("com.openAF.hostName")
    val servicesPort = context.getProperty("com.openAF.servicesPort").toInt
    println(s"Starting RMI Client (connecting to $hostName:$servicesPort)...")
    val client = new RMIClient(hostName, servicesPort)
    client.connectBlocking()
    val servicesListingClass = classOf[ServicesListing]
    val servicesListing = client.proxy(servicesListingClass)
    val services = servicesListing.services.filterNot(_ == servicesListingClass.getName)
    val packagesToBundleMap = packagesToBundle(context)
    services.foreach(serviceName => {
      loadClass(serviceName, packagesToBundleMap) match {
        case Some(serviceClass:Class[Any]) => {
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
    bundles.flatMap(bundle => {
      val exportPackageStringOption = Option(bundle.getHeaders.get("Export-Package"))
      exportPackageStringOption.map(exportPackageString => {
        exportPackageString.replaceAll("""uses:=\"(\\.|[^\"])*\"""", "")
      }).map(_ -> bundle)
    }).toMap
  }

  private def loadClass(className:String, packagesToBundle:Map[String,Bundle]) = {
    val classPackage = className.substring(0, className.lastIndexOf('.'))
    packagesToBundle.find{case (packageString, bundle) => packageString.contains(classPackage)}.map{case ((_,bundle)) => {
      bundle.loadClass(className)
    }}
  }
}
