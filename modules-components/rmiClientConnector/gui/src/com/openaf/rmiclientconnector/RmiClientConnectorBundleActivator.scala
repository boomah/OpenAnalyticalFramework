package com.openaf.rmiclientconnector

import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.rmi.client.RMIClient
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}

class RmiClientConnectorBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    val hostName = context.getProperty("openAF.hostName")
    val servicesPort = context.getProperty("openAF.servicesPort").toInt
    println("Starting RMI Client (connecting to %s:%s)...".format(hostName, servicesPort))
    val client = new RMIClient(hostName, servicesPort)
    val connectFuture = client.connect
    connectFuture.addListener(new ChannelFutureListener {
      def operationComplete(future:ChannelFuture) {
        println("^^^ Connection Future finished!!! " + (connectFuture.isSuccess, connectFuture.isDone, connectFuture.getCause))
      }
    })
  }

  def stop(context:BundleContext) {}
}
