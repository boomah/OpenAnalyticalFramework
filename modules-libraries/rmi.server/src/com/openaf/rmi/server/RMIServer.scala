package com.openaf.rmi.server

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory
import java.util.concurrent.Executors
import org.jboss.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress

class RMIServer(port:Int) {
  private val factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
  private val bootstrap = new ServerBootstrap(factory)
  bootstrap.setPipelineFactory(new ServerPipelineFactory)
  bootstrap.setOption("child.tcpNoDelay", true)
  bootstrap.setOption("child.keepAlive", true)

  def start = {
    bootstrap.bind(new InetSocketAddress(port))
  }
}
