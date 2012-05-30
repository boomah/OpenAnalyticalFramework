package com.openaf.rmi.client

import org.jboss.netty.bootstrap.ClientBootstrap
import java.util.concurrent.Executors
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory

class RMIClient(hostName:String, port:Int) {
  def connect() {
    val factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),Executors.newCachedThreadPool())
    val bootstrap = new ClientBootstrap(factory)
  }
}
