package com.openaf.rmiclientconnector

import org.osgi.framework.{BundleContext, BundleActivator}
import com.openaf.rmi.client.RMIClient
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}

class RmiClientConnectorBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    println("Starting RMI Client...")
    val hostName = context.getProperty("openAF.hostName")
    val servicePort = context.getProperty("openAF.servicePort").toInt
    val client = new RMIClient(hostName, servicePort)
    val connectFuture = client.connect
    connectFuture.addListener(new ChannelFutureListener {
      def operationComplete(future:ChannelFuture) {
        println("^^^ Connection Future finished!!! " + (connectFuture.isSuccess, connectFuture.isDone, connectFuture.getCause))
      }
    })
  }

  def stop(context:BundleContext) {}
}
