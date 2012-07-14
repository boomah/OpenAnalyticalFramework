package com.openaf.rmi.server

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import java.util.concurrent.Executors
import org.jboss.netty.bootstrap.ServerBootstrap
import java.net.InetSocketAddress
import org.jboss.netty.channel.Channel

class RMIServer(port:Int) {
  private val factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
  private val bootstrap = new ServerBootstrap(factory)
  bootstrap.setPipelineFactory(new ServerPipelineFactory)
  bootstrap.setOption("child.tcpNoDelay", true)
  bootstrap.setOption("child.keepAlive", true)

  private var channel:Channel = _

  def start() {
    channel = bootstrap.bind(new InetSocketAddress(port))
  }

  def stop() {
    Option(channel).foreach(chan => {
      chan.close.await
    })
    bootstrap.releaseExternalResources()
  }
}
