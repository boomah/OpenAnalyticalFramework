package com.openaf.rmi.client

import org.jboss.netty.bootstrap.ClientBootstrap
import java.util.concurrent.Executors
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import java.net.InetSocketAddress

class RMIClient(hostName:String, port:Int) {
  private val factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
  private val bootstrap = new ClientBootstrap(factory)
  bootstrap.setPipelineFactory(new ClientPipelineFactory)
  bootstrap.setOption("tcpNoDelay", true)
  bootstrap.setOption("keepAlive", true)

  def connect = {
    bootstrap.connect(new InetSocketAddress(hostName, port))
  }
}
